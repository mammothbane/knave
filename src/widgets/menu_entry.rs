use std::hash::Hash;

use crate::widgets::StyledString;

#[derive(Debug, PartialEq, Clone)]
pub struct MenuEntry<T>
where
    T: Hash + PartialEq + Clone,
{
    text:       Vec<StyledString>,
    identifier: T,
}

impl<T> MenuEntry<T>
where
    T: Hash + PartialEq + Clone,
{
    pub fn new<S>(s: S, ident: T) -> Self
    where
        S: Into<StyledString>,
    {
        MenuEntry {
            text:       vec![s.into()],
            identifier: ident,
        }
    }

    pub fn new_multiple<S>(s: Vec<S>, ident: T) -> Self
    where
        S: Into<StyledString>,
    {
        MenuEntry {
            text:       s.into_iter().map(|ss| ss.into()).collect(),
            identifier: ident,
        }
    }

    pub fn text(&self) -> &[StyledString] {
        &self.text
    }

    pub fn identifier(&self) -> &T {
        &self.identifier
    }
}
