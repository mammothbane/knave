use crate::{
    RenderState,
    Result,
};

pub enum ModeTransition {
    NewMode(Box<dyn GameMode>),
    None,
    Quit,
}

pub trait GameMode {
    fn name(&self) -> String;

    fn enter(&mut self) {}
    fn exit(&mut self) {}

    fn step(&mut self, event: termion::event::Event) -> ModeTransition;
    fn render(&mut self, rs: &mut RenderState) -> Result<()>;
}
