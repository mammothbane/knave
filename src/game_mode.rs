use crate::{
    RenderState,
    Result,
};

pub trait GameMode {
    fn enter(&mut self) {}
    fn exit(&mut self) {}

    fn step(&mut self, event: termion::event::Event) -> Option<Box<dyn GameMode>>;
    fn render(&self, rs: &mut RenderState) -> Result<()>;
}
