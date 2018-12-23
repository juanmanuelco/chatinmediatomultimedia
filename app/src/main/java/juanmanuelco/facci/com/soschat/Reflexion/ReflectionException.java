package juanmanuelco.facci.com.soschat.Reflexion;

public class ReflectionException extends RuntimeException {
    private static final long serialVersionUID = -5110160439913415481L;
    /**
     * Constructor.
     *
     * @param e
     *            exception thrown by <code>java.lang.reflection</code>.
     */
    public ReflectionException(final Exception e) {
        super(e);
    }
}
