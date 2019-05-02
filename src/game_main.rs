use crossbeam_channel::{
    self,
    Receiver,
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
    event_tx:   Sender<Action>,
}

impl<'a, 'b> GameMain<'a, 'b> {
    pub fn new() -> Self {
        let mut world = World::new();
        let dispatcher = DispatcherBuilder::new().build();

        let (tx, rx) = crossbeam_channel::bounded::<Action>(64);

        world.add_resource(rx);
        world.add_resource(Buffer::empty(Rect::new(0, 0, 0, 0)));

        GameMain {
            world,
            dispatcher,
            event_tx: tx,
        }
    }
}

impl<'a, 'b> GameMode for GameMain<'a, 'b> {
    #[inline]
    fn name(&self) -> String {
        "Main".to_owned()
    }

    fn render(&mut self, rs: &mut RenderState) -> Result<()> {
        use std::borrow::Borrow;

        rs.terminal.draw(move |mut f| {
            use crate::raw_buffer::RawBuffer;
            use tui::widgets::Widget;

            let size = f.size();

            self.world.res.fetch_mut::<Buffer>().resize(f.size());

            // TODO: run render system

            let buf = self.world.res.fetch::<Buffer>();
            let buf = buf.borrow();

            RawBuffer(buf).render(&mut f, size);
        })?;

        Ok(())
    }

    fn step(&mut self, evt: termion::event::Event) -> ModeTransition {
        use crossbeam_channel::TrySendError;
        use log::warn;

        let action = DefaultBindings::translate(evt);

        if action != Action::NotMapped {
            match self.event_tx.try_send(action) {
                Err(TrySendError::Disconnected(_)) => {},
                Err(TrySendError::Full(action)) => {
                    warn!("dropping action {:?} because channel was full", action);
                },
                _ => {},
            }
        }

        ModeTransition::None
    }
}
