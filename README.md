# javafx-one-time-listener
This is an extension to the JavaFX ChangeListener interface to support single-use listeners. These kisteners will execute only when the new value of the observable matches some predicate. After execution, the listener will be removed and discarded. A few factory methods have been added which cover many of the common  use-cases and to offer a convenient way of transforming an existing ChangeListener into a OneTimeListener.

