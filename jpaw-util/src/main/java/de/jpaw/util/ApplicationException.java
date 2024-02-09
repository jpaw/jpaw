/*
 * Copyright 2012 Michael Bischoff
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.jpaw.util;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parent class of all related exception codes.
 *
 * Defines the parent class for all application errors which are
 * thrown in the marshalling / unmarshalling or messaging areas, as well as
 * the application modules themselves.
 * <p>
 * Error codes are defined in a way such that the 8th digit (error code divided by 10 to the power of 8)
 * provides a good classification of the problem.
 * The classifications provided are actually targeting at full application coverage and not only message serialization / deserialization.
 *
 */

public class ApplicationException extends RuntimeException {
    private static final long serialVersionUID = 1122421467960337766L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationException.class);


    /** The classification of return codes indicating success (which would never be instantiated as an exception). */
    public static final int SUCCESS = 0;

    /**
     * The classification of return codes indicating success (which would never be instantiated as an exception).
     */
    public static final int CL_SUCCESS = 0;

    /**
     * The classification of return codes indicating a decline or negative decision,
     * without being a parameter or processing problem, and therefore also never be instantiated as an exception.
     */
    public static final int CL_DENIED  = 1;

    /**
     * The classification of return codes indicating an invalid message format.
     */
    public static final int CL_PARSER_ERROR = 2;

    /**
     * The classification of return codes indicating an invalid reference or field value (for example an invalid customer no or invalid country code).
     */
    public static final int CL_PARAMETER_ERROR = 3;

    /**
     * The classification of return codes indicating a processing timeout.
     * The requester should react by resending the request some time later.
     * The resource was available but did not respond back in the expected time.
     */
    public static final int CL_TIMEOUT = 4;

    /**
     * The classification of return codes indicating a (hopefully temporary) problem of resource shortage
     * (no free sockets, disk full, cannot fork due to too many processes...).
     */
    public static final int CL_RESOURCE_EXHAUSTED = 5;

    /**
     * The classification of return codes indicating a resource or service which is temporarily unavailable.
     * This could be due to a downtime of an OSGi component or remote service.
     * Senders should treat such return code similar to a timeout return code and retry later.
     */
    public static final int CL_SERVICE_UNAVAILABLE = 6;

    /**
     * An intermediate classification returned by internal validation algorithms such as bonaparte validation or Java Bean Validation.
     * Contentwise, this is a subset of the <code>PARAMETER_ERROR</code> range, but these codes will most likely be caught
     * and mapped to more generic return codes, or used as user feedback in the UI.
     */
    public static final int CL_VALIDATION_ERROR = 7;

    /**
     * The classification of return codes indicating failure of an internal plausibility check.
     * This should never happen and therefore usually indicates a programming error.
     */
    public static final int CL_INTERNAL_LOGIC_ERROR = 8;  // assertion failed

    /**
     * The classification of problems occurring in the persistence layer (usually database),
     * which have not been caught by a specific exception handler.
     * This can be due to resource exhaustion, but also programming errors.
     * Usually deeper investigation is required.
     * Callers receiving this code should retry at maximum one time, and then defer the request and queue it into a manual analysis queue.
     */
    public static final int CL_DATABASE_ERROR = 9;

    /** The classification which specifies that no result has been returned, but a future. */
    public static final int CL_FUTURE = 20;


    /**
     * The factor by which the classification code is multiplied.
     * An error code modulus the classification factor gives details about where and why the problem occurred.
     */
    public static final int CLASSIFICATION_FACTOR = 100000000;

    private static final Map<Integer, String> CODE_TO_DESCRIPTION = new ConcurrentHashMap<>(2000);
    private static final Map<Integer, Integer> DUPLICATE_CODE_COUNTER = new ConcurrentHashMap<>(2000);
    private static final Integer ONE = Integer.valueOf(1);

    /**
     * Provides the mapping of error codes to textual descriptions. It is the responsibility of superclasses
     * inheriting this class to populate this map for the descriptions of the codes they represent.
     * It is recommended to perform such initialization not during class load, but lazily, once the first exception is thrown.
     */
    @Deprecated
    protected static final class DuplicateCheckingMap {

        @Deprecated  // use registerCode()
        public void put(final int errorCode, final String description) {
            final Integer errorCodeBoxed = Integer.valueOf(errorCode);
            if (errorCode < 0 || errorCode > 10 * CLASSIFICATION_FACTOR) {
                LOGGER.error("Attempted to create error message out of range for {}: {}", errorCodeBoxed, description);
                throw new IllegalArgumentException("out of range");
            }
            if ((errorCode % 10000) == 0) {
                LOGGER.error("Attempted to create error message with zero module offset for {}: {}", errorCodeBoxed, description);
                throw new IllegalArgumentException("module offset");
            }
            // Validate that the exception code has not been used before. In case it has, throw an exception.
            final String oldDescription = CODE_TO_DESCRIPTION.put(errorCodeBoxed, description);
            if (oldDescription != null) {
                LOGGER.error("Overwriting error message for {} with '{}' (previously '{}')", errorCodeBoxed, description, oldDescription);
                // throw new IllegalArgumentException("duplicate error code");
            }
            DUPLICATE_CODE_COUNTER.merge(errorCode % CLASSIFICATION_FACTOR, ONE, (a, b) -> (a + b));
        }
        @Deprecated  // use codeToString()
        public String get(final int errorCode) {
            return CODE_TO_DESCRIPTION.get(errorCode);
        }
        @Deprecated  // rewrite code to use forEachCode() instead
        public Set<Map.Entry<Integer, String>> entrySet() {
            return CODE_TO_DESCRIPTION.entrySet();
        }
    }

    protected static DuplicateCheckingMap codeToDescription = new DuplicateCheckingMap();

    public static void registerCode(final int errorCode, final String description) {
        codeToDescription.put(errorCode, description);
    }

    public static void forEachCode(final BiConsumer<Integer, String> processor) {
        for (final Map.Entry<Integer, String> e: CODE_TO_DESCRIPTION.entrySet()) {
            processor.accept(e.getKey(), e.getValue());
        }
    }

    /**
     * Checks all stored codes for duplicate values - including duplicates of just the base value.
     */
    public static boolean checkForDuplicates() {
        boolean foundDuplicates = false;
        for (Map.Entry<Integer, Integer> codeAndCount: DUPLICATE_CODE_COUNTER.entrySet()) {
            if (codeAndCount.getValue() > 1) {
                LOGGER.error("Code {} has been used {} times", codeAndCount.getKey(), codeAndCount.getValue());
                foundDuplicates = true;
                // Now list all (not yet overwritten) entries. Note this could be a single entry if the codes matched exactly.
                for (int i = 0; i < 10; ++i) {
                    final int codeWithClassification = (i * CLASSIFICATION_FACTOR) + codeAndCount.getKey();
                    final String desc = CODE_TO_DESCRIPTION.get(codeWithClassification);
                    if (desc != null) {
                        LOGGER.error("    Code {}: '{}'", codeWithClassification, desc);
                    }
                }
            }
        }
        return foundDuplicates;
    }

    private final int errorCode;      // the unique 9 digit exception code
    private final String fieldName;   // if known, the name of the field where the error occurred
    private final String className;   // if known, the name of the class which contained the field
    private final Integer index;      // if application, a character index or array index

    /** Returns the error code for this exception */
    public final int getErrorCode() {
        return errorCode;
    }

    /** Creates a new ApplicationException for a given error code, plus additional information. */
    public ApplicationException(final int errorCode, final String fieldName, final String className, final Integer index) {
        super();
        this.errorCode = errorCode;
        this.fieldName = fieldName;
        this.className = className;
        this.index = index;
    }

    public ApplicationException(final int errorCode) {
        this(errorCode, null, null, null);
    }

    /** Creates a new ApplicationException for a given error code, with some explanatory details. */
    public ApplicationException(final int errorCode, final String detailedMessage) {
        super("Code " + Integer.toString(errorCode) + (detailedMessage == null ? "" : " @ " + detailedMessage));
        this.errorCode = errorCode;
        this.fieldName = null;
        this.className = null;
        this.index = null;
    }

    // some boilerplate code to retrieve exception properties
    public int getIndex() {
        return index;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getClassName() {
        return className;
    }

    /** Returns the classification code for this exception. */
    public final int getClassification() {
        return (errorCode / CLASSIFICATION_FACTOR);
    }

    /** Returns information if a code is an "OK" code. */
    public static boolean isOk(final int returnCode) {
        return returnCode >= 0 && returnCode < CLASSIFICATION_FACTOR;
    }

    /** Returns information if a code is a "Future" code. */
    public static boolean isFuture(final int returnCode) {
        return returnCode >= CL_FUTURE;
    }

    /** returns a text representation of an error code, independent of an existing exception */
    public static String codeToString(final int code) {
        final String msg = CODE_TO_DESCRIPTION.get(Integer.valueOf(code));
        return msg != null ? msg : "unknown code";
    }

    /** Returns a textual description of the error code.
     *  The method is declared as final as long as it's used from the constructors of superclasses.
     *
     * @return the textual description.
     */
    public final String getStandardDescription() {
        return codeToString(errorCode);
    }

    /** Returns a textual description of the exception.
     *
     * @return the textual description.
     */
    @Override
    public String toString() {
        return super.toString() + ": (" + getStandardDescription() + ")";
    }

    /**
     * Creates a localized description of the standard message
     * Subclasses may override this method in order to produce a
     * locale-specific message.  For subclasses that do not override this
     * method, the default implementation returns the same result as
     * {@code getStandardMessage()}.
     *
     * @return  The localized description of this ApplicationException.
     */
    public String getLocalizedStandardDescription() {
        return getStandardDescription();
    }
}
