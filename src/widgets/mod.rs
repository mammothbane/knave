use tui::style::Style;

pub use menu::*;
pub use menu_entry::*;
pub use raw_buffer::*;

mod menu;
mod menu_entry;
mod raw_buffer;

#[derive(Debug, Clone, PartialEq)]
pub struct StyledString(String, Style);

#[allow(dead_code)]
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
