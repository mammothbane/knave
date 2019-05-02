use specs::prelude::*;
use tui::buffer::Buffer;

pub struct RenderSystem {}

impl<'a> System<'a> for RenderSystem {
    type SystemData = (Write<'a, Buffer>);

    fn run(&mut self, data: Self::SystemData) {
        let (buf) = data;
    }
}
