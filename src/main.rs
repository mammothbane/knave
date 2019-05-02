#![feature(duration_float)]

use std::{
    io,
    time::{
        Duration,
        Instant,
    },
};

use lazy_static::lazy_static;
use termion::{
    input::TermRead,
    raw::IntoRawMode,
};
use tui::backend::TermionBackend;

pub(crate) use error::*;
pub(crate) use game_mode::*;

pub type Terminal = tui::Terminal<TermionBackend<termion::raw::RawTerminal<io::Stdout>>>;

mod error;
mod game_main;
mod game_mode;
mod input;
mod logging;
mod menu;
mod raw_buffer;
mod start_menu;

pub struct RenderState {
    pub terminal: Terminal,
}

fn main() -> Result<()> {
    use self::start_menu::StartMenu;
    use log::info;

    logging::init().expect("initializing logging");

    let stdout = io::stdout().into_raw_mode()?;
    let backend = TermionBackend::new(stdout);
    let mut terminal = Terminal::new(backend)?;

    terminal.clear()?;
    terminal.hide_cursor()?;
    terminal.autoresize()?;

    info!("terminal initialized");

    let mut rs = RenderState {
        terminal,
    };

    let mut events = termion::async_stdin().events();
    let mut current_mode: Box<dyn GameMode> = Box::new(StartMenu::default());

    let mut last_render_time;

    const FPS_CAP: usize = 90;
    lazy_static! {
        static ref SEC_PER_FRAME: Duration = Duration::from_secs_f64(1. / FPS_CAP as f64);
    }

    info!("entering main loop");
    loop {
        rs.terminal.autoresize()?;
        current_mode.render(&mut rs)?;
        last_render_time = Instant::now();

        loop {
            match events.next() {
                Some(evt) => {
                    for e in evt.into_iter() {
                        match current_mode.step(e) {
                            ModeTransition::NewMode(mode) => current_mode = mode,
                            ModeTransition::None => {},
                            ModeTransition::Quit => {
                                info!("quitting (invoked by {})", current_mode.name());

                                rs.terminal.clear()?;
                                rs.terminal.show_cursor()?;
                                rs.terminal.flush()?;

                                return Ok(());
                            },
                        };
                    }
                },
                None => {
                    break;
                },
            };
        }

        let diff = Instant::now() - last_render_time;
        if diff <= Duration::new(0, 0) {
            continue;
        }

        std::thread::sleep(diff);
    }
}
