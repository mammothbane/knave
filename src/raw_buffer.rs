use tui::{
    buffer::Buffer,
    layout::Rect,
    widgets::Widget,
};

/// A widget to allow the user to draw directly to the target buffer.
///
/// Area indications are ignored--the full buffer passed to `draw` is always
/// used.
#[derive(Debug, Clone, PartialEq)]
pub struct RawBuffer<'a>(pub &'a Buffer);

impl<'a> Widget for RawBuffer<'a> {
    fn draw(&mut self, _: Rect, buf: &mut Buffer) {
        buf.merge(self.0)
    }
}
