use termion::event::Event;

#[derive(Debug, Copy, Clone, PartialEq, Eq, PartialOrd, Ord, Hash)]
pub enum Action {
    Up,
    Down,
    Left,
    Right,
    Interact,
    Skip,
    Cancel,

    NotMapped,
}

impl Default for Action {
    #[inline]
    fn default() -> Self {
        Action::NotMapped
    }
}

pub trait InputBindings {
    fn translate(e: Event) -> Action;
}

#[derive(Default, Debug, Copy, Clone, PartialEq, Eq, PartialOrd, Ord, Hash)]
pub struct DefaultBindings;

impl InputBindings for DefaultBindings {
    #[inline]
    fn translate(e: Event) -> Action {
        use termion::event::Key::*;
        use Event::*;

        match e {
            Key(Up) | Key(Char('w')) => Action::Up,
            Key(Left) | Key(Char('a')) => Action::Left,
            Key(Down) | Key(Char('s')) => Action::Down,
            Key(Right) | Key(Char('d')) => Action::Right,
            Key(Char('e')) | Key(Char('\n')) => Action::Interact,
            Key(Char(' ')) => Action::Skip,
            Key(Ctrl('[')) => Action::Cancel,
            _ => Action::NotMapped,
        }
    }
}
