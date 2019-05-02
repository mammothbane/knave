use specs::prelude::*;
use tui::{
    buffer::Buffer,
    layout::{
        Constraint,
        Direction,
        Layout,
    },
    widgets::{
        Block,
        Borders,
        Widget,
    },
};

pub struct RenderSystem {}

impl<'a> System<'a> for RenderSystem {
    type SystemData = (Write<'a, Buffer>);

    fn run(&mut self, data: Self::SystemData) {
        let (mut buf) = data;

        let chunks = Layout::default()
            .direction(Direction::Horizontal)
            .constraints([Constraint::Percentage(80), Constraint::Percentage(20)].as_ref())
            .split(*buf.area());

        let left_pane = chunks[0];
        let right_pane = chunks[1];

        let chunks = Layout::default()
            .direction(Direction::Vertical)
            .constraints([Constraint::Percentage(80), Constraint::Percentage(20)].as_ref())
            .split(left_pane);

        let main_pane = chunks[0];
        let messages_pane = chunks[1];

        Block::default()
            .borders(Borders::ALL)
            .draw(main_pane, &mut buf);

        Block::default()
            .borders(Borders::LEFT | Borders::RIGHT | Borders::BOTTOM)
            .draw(messages_pane, &mut buf);

        Block::default()
            .borders(Borders::TOP | Borders::RIGHT | Borders::BOTTOM)
            .draw(right_pane, &mut buf);
    }
}
