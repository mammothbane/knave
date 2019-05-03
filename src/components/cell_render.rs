use specs_derive::Component;
use tui::widgets::Cell;

#[derive(Component, Debug, Clone)]
pub struct CellRender(Cell);
