use specs::prelude::*;
use termion::{
    input::TermRead,
    raw::IntoRawMode,
};
use tui::backend::TermionBackend;

use std::io;

pub(crate) use error::*;
pub(crate) use game_mode::*;

pub type Terminal = tui::Terminal<TermionBackend<termion::raw::RawTerminal<io::Stdout>>>;

mod error;
mod game_mode;
mod input;
mod log;
mod menu;
mod start_menu;

pub struct RenderState {
    pub terminal: Terminal,
}

fn main() -> Result<()> {
    use self::start_menu::StartMenu;

    log::init().expect("initializing logging");

    let stdout = io::stdout().into_raw_mode()?;
    let backend = TermionBackend::new(stdout);
    let mut terminal = Terminal::new(backend)?;

    terminal.clear()?;
    terminal.hide_cursor()?;
    terminal.autoresize()?;

    let mut rs = RenderState {
        terminal,
    };

    let mut events = io::stdin().events();
    let mut current_mode: Box<dyn GameMode> = Box::new(StartMenu::default());
    loop {
        rs.terminal.autoresize()?;
        current_mode.render(&mut rs)?;

        match events.next() {
            Some(evt) => {
                for e in evt.into_iter() {
                    match current_mode.step(e) {
                        Some(mode) => {
                            current_mode = mode;
                        },
                        _ => {},
                    };
                }
            },
            None => {},
        };
    }
}
