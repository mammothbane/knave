use std::cell::RefCell;

use lazy_static::lazy_static;
use tui::{
    layout::Rect,
    widgets::Text,
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

static KNAVE_STR: &'static str = r#" _        _        _______           _______
| \    /\( (    /|(  ___  )|\     /|(  ____ \
|  \  / /|  \  ( || (   ) || )   ( || (    \/
|  (_/ / |   \ | || (___) || |   | || (__
|   _ (  | (\ \) ||  ___  |( (   ) )|  __)
|  ( \ \ | | \   || (   ) | \ \_/ / | (
|  /  \ \| )  \  || )   ( |  \   /  | (____/\
|_/    \/|/    ) )|/     \|   \_/   (_______/"#;

static KNAVE_STR_SMALL: &'static str = "K N A V E";

lazy_static! {
    static ref KNAVE_LINES: Vec<&'static str> = KNAVE_STR.split('\n').collect();
    static ref KNAVE_DIMENS: Rect = {
        let longest_line = KNAVE_LINES.iter().map(|line| line.len()).max().unwrap();
        let height = KNAVE_LINES.len();

        Rect::new(0, 0, longest_line as u16, height as u16)
    };
    static ref KNAVE_RAW: Text<'static> = Text::raw(KNAVE_STR);
}

pub(crate) struct StartMenu {
    menu: RefCell<Menu<StartMenuOption>>,
}

#[derive(Copy, Clone, PartialEq, Eq, PartialOrd, Ord, Hash, Debug)]
enum StartMenuOption {
    New,
    Load,
    Quit,
}

impl GameMode for StartMenu {
    #[inline]
    fn name(&self) -> String {
        "Start Menu".to_owned()
    }

    fn render(&mut self, rs: &mut RenderState) -> Result<()> {
        use tui::{
            layout::{
                Constraint,
                Direction,
                Layout,
            },
            widgets::{
                Paragraph,
                Widget,
            },
        };

        rs.terminal.draw(|mut f| {
            let chunks = Layout::default()
                .direction(Direction::Vertical)
                .margin(0)
                .constraints([Constraint::Percentage(75), Constraint::Percentage(25)].as_ref())
                .split(f.size());

            let splash_chunk = chunks[0];
            let menu_chunk = chunks[1];

            self.menu.borrow_mut().render(&mut f, menu_chunk);

            let center_x = splash_chunk.x + splash_chunk.width / 2;
            let center_y = splash_chunk.y + splash_chunk.height / 2;

            if splash_chunk.height >= KNAVE_DIMENS.height
                && splash_chunk.width >= KNAVE_DIMENS.width
            {
                let text_start_x = center_x - KNAVE_DIMENS.width / 2;
                let text_start_y = center_y - KNAVE_DIMENS.height / 2;

                let text = [&*KNAVE_RAW];
                let text_chunk = Rect::new(
                    text_start_x,
                    text_start_y,
                    KNAVE_DIMENS.width,
                    KNAVE_DIMENS.height,
                );
                Paragraph::new(text.iter().map(|x| *x))
                    .wrap(false)
                    .render(&mut f, text_chunk);
            } else if splash_chunk.height >= 1 && splash_chunk.width >= KNAVE_STR_SMALL.len() as u16
            {
                let text_start_x = center_x - ((KNAVE_STR_SMALL.len() / 2) as u16);

                let text = [Text::raw(KNAVE_STR_SMALL)];
                let text_chunk = Rect::new(text_start_x, center_y, KNAVE_STR_SMALL.len() as u16, 1);

                Paragraph::new(text.iter())
                    .wrap(false)
                    .render(&mut f, text_chunk);
            }
        })?;

        Ok(())
    }

    fn step(&mut self, event: termion::event::Event) -> ModeTransition {
        use crate::{
            game::GameMain,
            input::{
                Action,
                DefaultBindings,
                InputBindings,
            },
        };

        match DefaultBindings::translate(event) {
            Action::Up => {
                self.menu.borrow_mut().up();
            },
            Action::Down => {
                self.menu.borrow_mut().down();
            },
            Action::Interact => {
                match self.menu.borrow().select() {
                    StartMenuOption::New => {
                        return ModeTransition::NewMode(Box::new(GameMain::new()));
                    },
                    StartMenuOption::Load => {},
                    StartMenuOption::Quit => {
                        return ModeTransition::Quit;
                    },
                };
            },
            Action::Quit => {
                return ModeTransition::Quit;
            },
            _ => {},
        }

        ModeTransition::None
    }
}

impl Default for StartMenu {
    fn default() -> Self {
        use crate::widgets::StyledString;
        use tui::style::{
            Color,
            Style,
        };
        use StartMenuOption::*;

        let entries = vec![
            MenuEntry::new("New", New),
            MenuEntry::new("Load", Load),
            MenuEntry::new("Quit", Quit),
        ];
        StartMenu {
            menu: RefCell::new(Menu::new(
                entries,
                0,
                StyledString::new("=>", Style::default().fg(Color::Blue)),
            )),
        }
    }
}
