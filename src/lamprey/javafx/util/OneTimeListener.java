package lamprey.javafx.util;

import java.util.Objects;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * This is a ChangeListener which will execute only when the newValue of the observable
 * matches the given predicate. After execution, the listener will be removed and
 * discarded. A few factory methods have been added which cover many of the common 
 * use-cases and offer a convenient way to transform an existing ChangeListener into a
 * OneTimeListener.
 *
 * @param <T> Generic type of the observable within this listener.
 *
 * @author Griffin O'Neill
 */
@FunctionalInterface
public interface OneTimeListener<T> extends ChangeListener<T>
{
    /**
     * Creates a OneTimeListener that runs only once as soon as its triggered.
     * @param onExecute The function to execute when the listener triggers.
     * @return A OneTimeListener that accepts an argument and runs when triggered.
     */
    public static <T> OneTimeListener<T> create(ChangeListener<? super T> onExecute)
    {
        return onExecute::changed;
    }

    /**
     * Creates a OneTimeListener that only runs once the given value becomes non-null.
     * @param onExecute The function to execute when the run condition triggers.
     * @return A OneTimeListener that accepts an argument and runs when non-null.
     */
    public static <T> OneTimeListener<T> runWhenNonNull(ChangeListener<? super T> onExecute)
    {
        return new OneTimeListener<>()
        {
            @Override
            public void onExecute(ObservableValue<? extends T> observable, T oldValue, T newValue)
            {
                onExecute.changed(observable, oldValue, newValue);
            }

            public boolean runCondition(ObservableValue<? extends T> observable, T oldValue,
                    T newValue)
            {
                return newValue != null;
            }
        };
    }

    /**
     * Creates a OneTimeListener that only runs once the given value becomes null.
     * @param onExecute The function to execute when the run condition triggers.
     * @return A OneTimeListener that accepts an argument and runs when null.
     */
    public static <T> OneTimeListener<T> runWhenNull(ChangeListener<? super T> onExecute)
    {
        return new OneTimeListener<>()
        {
            @Override
            public void onExecute(ObservableValue<? extends T> observable, T oldValue, T newValue)
            {
                onExecute.changed(observable, oldValue, newValue);
            }

            public boolean runCondition(ObservableValue<? extends T> observable, T oldValue,
                    T newValue)
            {
                return newValue == null;
            }
        };
    }

    /**
     * Creates a OneTimeListener that only runs once value becomes equal to the given value.
     * @param onExecute The function to execute when the run condition triggers.
     * @param value The value to compare against.
     * @return A OneTimeListener that runs when the observable value is equal to the passed-in
     *         value.
     */
    public static <T> OneTimeListener<T> runWhenEqualTo(ChangeListener<? super T> onExecute,
            T value)
    {
        return new OneTimeListener<>()
        {
            @Override
            public void onExecute(ObservableValue<? extends T> observable, T oldValue, T newValue)
            {
                onExecute.changed(observable, oldValue, newValue);
            }

            public boolean runCondition(ObservableValue<? extends T> observable, T oldValue,
                    T newValue)
            {
                return Objects.equals(newValue, value);
            }
        };
    }

    /**
     * Creates a OneTimeListener that only runs once value is not equal to the given value.
     * @param onExecute The function to execute when the run condition triggers.
     * @param value The value to compare against.
     * @return A OneTimeListener that runs when the observable value is not equal to the passed-in
     *         value.
     */
    public static <T> OneTimeListener<T> runWhenNotEqualTo(ChangeListener<? super T> onExecute,
            T value)
    {
        return new OneTimeListener<>()
        {
            @Override
            public void onExecute(ObservableValue<? extends T> observable, T oldValue, T newValue)
            {
                onExecute.changed(observable, oldValue, newValue);
            }

            public boolean runCondition(ObservableValue<? extends T> observable, T oldValue,
                    T newValue)
            {
                return !Objects.equals(newValue, value);
            }
        };
    }

    /**
     * Creates a OneTimeListener that only runs once the given boolean value becomes true.
     * @param onExecute The function to execute when the run condition triggers.
     * @return A OneTimeListener that accepts a boolean argument.
     */
    public static OneTimeListener<Boolean> runWhenTrue(ChangeListener<? super Boolean> onExecute)
    {
        return new OneTimeListener<>()
        {
            @Override
            public void onExecute(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                    Boolean newValue)
            {
                onExecute.changed(observable, oldValue, newValue);
            }

            public boolean runCondition(ObservableValue<? extends Boolean> observable,
                    Boolean oldValue, Boolean newValue)
            {
                return newValue;
            }
        };
    }

    /**
     * Creates a OneTimeListener that only runs once the given boolean value becomes false.
     * @param onExecute The function to execute when the run condition triggers.
     * @return A OneTimeListener that accepts a boolean argument.
     */
    public static OneTimeListener<Boolean> runWhenFalse(ChangeListener<? super Boolean> onExecute)
    {
        return new OneTimeListener<>()
        {
            @Override
            public void onExecute(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                    Boolean newValue)
            {
                onExecute.changed(observable, oldValue, newValue);
            }

            public boolean runCondition(ObservableValue<? extends Boolean> observable,
                    Boolean oldValue, Boolean newValue)
            {
                return !newValue;
            }
        };
    }

    /**
     * The logic to execute when this listener is triggered. This is a functional-interface
     * method target for this interface.
     * @param newVal The new value passed into the ChangeListener by JavaFX.
     */
    void onExecute(ObservableValue<? extends T> observable, T oldValue, T newValue);

    /**
     * The run condition for this listener. By default, this runs as long as the value passed
     *        in is not null.
     * @details This method can be overridden to provide a more strict run condition.
     * @param newVal The new value passed into the ChangeListener by JavaFX.
     * @return True if this listener should run once then destroy itself, false otherwise.
     */
    default boolean runCondition(ObservableValue<? extends T> observable, T oldValue, T newValue)
    {
        return true;
    }

    /**
     * This is the method that JavaFX calls when the listener is triggered. It first calls 
     * {@link #runCondition} to test whether or not this listener should execute its main
     * logic. If true, the listener will be removed from the observable and the 
     * {@link #onExecute} method will be called.
     */
    @Override
    default void changed(ObservableValue<? extends T> observable, T oldValue, T newValue)
    {
        if (runCondition(observable, oldValue, newValue))
        {
            observable.removeListener(this);
            onExecute(observable, oldValue, newValue);
        }
    }
}
