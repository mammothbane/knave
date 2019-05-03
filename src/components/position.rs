use specs_derive::Component;

#[derive(Component, Debug, Copy, Clone, PartialEq, Eq, Hash)]
pub struct Position {
    pub x: u32,
    pub y: u32,
}
