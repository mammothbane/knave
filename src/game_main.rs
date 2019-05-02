use crossbeam_channel::{
    self,
    Sender,
};
use specs::prelude::*;
use tui::{
    buffer::Buffer,
    layout::Rect,
};

use crate::{
    input::{
        Action,
        DefaultBindings,
        InputBindings,
    },
    GameMode,
    ModeTransition,
    RenderState,
    Result,
};

pub struct GameMain<'a, 'b> {
    world:      World,
    dispatcher: Dispatcher<'a, 'b>,
}

impl<'a, 'b> GameMain<'a, 'b> {
    pub fn new() -> Self {
        let mut world = World::new();
        let dispatcher = DispatcherBuilder::new().build();

        world.add_resource(Buffer::empty(Rect::new(0, 0, 0, 0)));
        world.add_resource(Action::NotMapped);

        GameMain {
            world,
            dispatcher,
        }
    }
}

impl<'a, 'b> GameMode for GameMain<'a, 'b> {
    #[inline]
    fn name(&self) -> String {
        "Main".to_owned()
    }

    fn enter(&mut self) {}

    fn exit(&mut self) {}

    fn render(&mut self, rs: &mut RenderState) -> Result<()> {
        use crate::systems::RenderSystem;
        use std::borrow::{
            Borrow,
            BorrowMut,
        };

        rs.terminal.draw(move |mut f| {
            use crate::raw_buffer::RawBuffer;
            use tui::widgets::Widget;

            let size = f.size();

            {
                let mut buf_mut = self.world.res.fetch_mut::<Buffer>();
                let buf_mut = buf_mut.borrow_mut();
                buf_mut.resize(f.size());
                buf_mut.reset();
            }

            RenderSystem {}.run_now(&self.world.res);

            let buf = self.world.res.fetch::<Buffer>();
            let buf = buf.borrow();

            RawBuffer(buf).render(&mut f, size);
        })?;

        Ok(())
    }

    fn step(&mut self, evt: termion::event::Event) -> ModeTransition {
        use std::borrow::BorrowMut;

        {
            let mut action = DefaultBindings::translate(evt);

            match action {
                Action::NotMapped => return ModeTransition::None,
                Action::Quit => return ModeTransition::Quit,
                _ => {},
            }

            let mut cur_action = self.world.write_resource::<Action>();
            let cur_action = cur_action.borrow_mut();

            std::mem::swap(&mut action, cur_action);
        }

        self.dispatcher.dispatch(&self.world.res);

        ModeTransition::None
    }
}
