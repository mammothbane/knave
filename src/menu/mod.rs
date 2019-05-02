use std::{
    convert::From,
    hash::Hash,
};

use tui::{
    buffer::Buffer,
    layout::Rect,
    style::Style,
    widgets::Widget,
};

pub use menu_entry::MenuEntry;

mod menu_entry;

#[derive(Debug, Clone, PartialEq)]
pub struct StyledString(String, Style);

impl StyledString {
    pub fn new<S>(s: S, style: Style) -> Self
    where
        S: AsRef<str>,
    {
        StyledString(s.as_ref().to_owned(), style)
    }

    #[inline]
    pub fn text(&self) -> &str {
        &self.0
    }

    #[inline]
    pub fn style(&self) -> &Style {
        &self.1
    }
}

impl<S> From<S> for StyledString
where
    S: AsRef<str>,
{
    fn from(s: S) -> Self {
        StyledString(s.as_ref().to_owned(), Style::default())
    }
}

#[derive(Debug, Clone, PartialEq)]
pub struct Menu<T>
where
    T: Hash + PartialEq + Clone,
{
    pointer: StyledString,
    entries: Vec<MenuEntry<T>>,
    index:   usize,
}

impl<T> Menu<T>
where
    T: Hash + PartialEq + Clone,
{
    pub fn new<E, S>(entries: E, start_index: usize, pointer: S) -> Self
    where
        E: AsRef<[MenuEntry<T>]>,
        S: Into<StyledString>,
    {
        Menu {
            pointer: pointer.into(),
            entries: entries.as_ref().to_owned(),
            index:   start_index,
        }
    }

    #[inline]
    pub fn up(&mut self) {
        self.index = ((((self.index + self.entries.len()) as isize) - 1)
            % (self.entries.len() as isize)) as usize;
    }

    #[inline]
    pub fn down(&mut self) {
        self.index = ((self.index + 1) % self.entries.len()) as usize;
    }

    #[inline]
    pub fn select(&self) -> &T {
        self.entries[self.index].identifier()
    }

    fn line_range(&self, max_height: Option<usize>) -> (usize, usize) {
        let (min_line, max_line) = max_height
            .map(|mh| {
                let idx = self.index as isize;
                let mh = mh as isize;

                // we have at most max_line lines to work with. we allocate half
                // rounded _down_ to the min-side. this means we'll always have
                // at least one line allocated on the max-side, which means we're always
                // displaying the line with the pointer on it, as desired.
                //
                // we then give the max-side as many lines as we have left. if there is any
                // space left over, we give this to the min-side. this will happen if we're near
                // the high end of a long list, such that the max-side is space limited but the
                // min-side is not.
                //
                // there is almost definitely a clever way to do this, but this is just
                // O(1) addition, so i don't care.

                let min_line = (idx - mh / 2).max(0);
                let minl_used = idx - min_line;
                let max_line = (idx + (mh - minl_used)).min(self.entries.len() as isize);
                let maxl_used = max_line - idx;
                let remainder = mh - (maxl_used + minl_used);

                assert!(remainder >= 0);

                let min_line = (min_line - remainder).max(0);

                (min_line as usize, max_line as usize)
            })
            .unwrap_or((0, self.entries.len() as usize));

        (min_line, max_line)
    }

    fn extents(&self, max_height: Option<usize>) -> (u16, u16) {
        let (min_line, max_line) = self.line_range(max_height);

        let width = self.entries[min_line..max_line]
            .iter()
            .map(|e| e.text().iter().map(|StyledString(s, _)| s.len()).sum())
            .max()
            .map(|width: usize| width + self.pointer.0.len() + 1)
            .unwrap_or(0);

        let height = max_height
            .map(|mh| mh.max(self.entries.len()))
            .unwrap_or(self.entries.len());

        (width as u16, height as u16)
    }

    #[inline]
    fn pointer_len(&self) -> u16 {
        return self.pointer.0.len() as u16;
    }
}

impl<T> Default for Menu<T>
where
    T: Hash + PartialEq + Clone,
{
    fn default() -> Self {
        Menu {
            pointer: "->".into(),
            entries: Vec::new(),
            index:   0,
        }
    }
}

impl<T> Widget for Menu<T>
where
    T: Hash + PartialEq + Clone,
{
    fn draw(&mut self, area: Rect, buf: &mut Buffer) {
        // don't bother rendering if we can't fit the pointer or if there are 0 lines
        if area.width < self.pointer_len() + 2 || area.height == 0 {
            return;
        }

        let (start_line, _end_line) = self.line_range(Some(area.height as usize));
        let (width, height) = self.extents(Some(area.height as usize));

        let max_text_width = width - (self.pointer_len() + 1);

        let text_start_x_offset = if width >= area.width {
            self.pointer_len() + 1
        } else {
            let area_center_x_offset = (area.width + 1) / 2 - 1;
            area_center_x_offset - ((width + 1) / 2 - 1)
        };

        let text_start_y_offset = if height >= area.height {
            0
        } else {
            let area_center_y_offset = (area.height + 1) / 2 - 1;
            area_center_y_offset - ((height + 1) / 2 - 1)
        };

        let text_start_x = area.x + text_start_x_offset;
        let text_start_y = area.y + text_start_y_offset;

        let pointer_y = area.y + ((self.index - start_line as usize) as u16);

        buf.set_stringn(
            text_start_x - (self.pointer_len() + 1),
            pointer_y,
            &self.pointer.0,
            area.width as usize,
            self.pointer.1.clone(),
        );

        self.entries.iter().enumerate().for_each(|(i, entry)| {
            let y = text_start_y + (i as u16);
            entry.text().iter().fold(0, |acc, StyledString(s, style)| {
                buf.set_stringn(
                    text_start_x + (acc as u16),
                    y,
                    &s,
                    max_text_width as usize - acc,
                    style.clone(),
                );

                acc + s.len()
            });
        });
    }
}

#[cfg(test)]
mod test {
    use super::*;

    #[test]
    fn test_line_range() {
        let entries = vec!["abc", "def", "ghi", "jkl", "mno", "pqr"]
            .into_iter()
            .map(|s| MenuEntry::new(s, 0i32))
            .cycle()
            .take(25)
            .collect::<Vec<_>>();

        // basic tests
        let menu = Menu::new(Vec::<MenuEntry<i32>>::new(), 0, "=>");
        assert_eq!((0, 0), menu.line_range(None));

        let menu = Menu::new(&entries[..1], 0, "=>");
        assert_eq!((0, 1), menu.line_range(None));

        let menu = Menu::new(&entries[..3], 0, "=>");
        assert_eq!((0, 3), menu.line_range(None));

        // test all index positions with a 6-entry list and a 4-line range
        let menu = Menu::new(&entries[..6], 0, "=>");
        assert_eq!((0, 4), menu.line_range(Some(4)));

        let menu = Menu::new(&entries[..6], 1, "=>");
        assert_eq!((0, 4), menu.line_range(Some(4)));

        let menu = Menu::new(&entries[..6], 2, "=>");
        assert_eq!((0, 4), menu.line_range(Some(4)));

        let menu = Menu::new(&entries[..6], 3, "=>");
        assert_eq!((1, 5), menu.line_range(Some(4)));

        let menu = Menu::new(&entries[..6], 4, "=>");
        assert_eq!((2, 6), menu.line_range(Some(4)));

        let menu = Menu::new(&entries[..6], 5, "=>");
        assert_eq!((2, 6), menu.line_range(Some(4)));


        let menu = Menu::new(&entries[..6], 0, "=>");
        assert_eq!((0, 0), menu.line_range(Some(0)));

        let menu = Menu::new(Vec::<MenuEntry<i32>>::new(), 0, "=>");
        assert_eq!((0, 0), menu.line_range(Some(10)));
    }
}
