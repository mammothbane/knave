use noise::{
    NoiseFn,
    Point2,
};

use crate::{
    widgets::{
        Menu,
        MenuEntry,
    },
    GameMode,
    ModeTransition,
    RenderState,
    Result,
};

pub struct ProcgenMode {
    noise: Vec<Box<NoiseFn<Point2<f64>>>>,
    menu:  Menu<NoiseType>,
}

#[derive(Copy, Clone, PartialEq, Eq, Debug, Hash)]
pub enum NoiseType {
    Simplex,
    Multifractal,
    Fbm,
}

impl ProcgenMode {
    pub fn new() -> Self {
        let entries = [];

        let menu = Menu::new(&entries, 0, "=>");

        ProcgenMode {
            menu,
            noise: Vec::new(),
        }
    }
}

impl GameMode for ProcgenMode {
    #[inline]
    fn name(&self) -> String {
        "Procgen".to_owned()
    }

    fn render(&mut self, rs: &mut RenderState) -> Result<()> {
        use tui::{
            layout::*,
            widgets::*,
        };

        rs.terminal.draw(|mut f| {
            let chunks = Layout::default()
                .direction(Direction::Vertical)
                .constraints([Constraint::Percentage(70), Constraint::Percentage(30)].as_ref())
                .split(f.size());

            let main_chunk = chunks[0];
            let adjustment_chunk = chunks[1];
        })?;

        Ok(())
    }

    fn step(&mut self, evt: termion::event::Event) -> ModeTransition {
        ModeTransition::None
    }
}
