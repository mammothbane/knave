use std::{
    error,
    fmt,
    io,
};

pub type Result<T> = std::result::Result<T, Error>;

#[derive(Debug)]
pub enum Error {
    IO(io::Error),
}

impl fmt::Display for Error {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{:?}", self)
    }
}

impl error::Error for Error {}

macro_rules! impl_from {
    ($t:ty, $variant:ident) => {
        impl From<$t> for Error {
            fn from(t: $t) -> Self {
                Error::$variant(t)
            }
        }
    };
}

impl_from!(io::Error, IO);
