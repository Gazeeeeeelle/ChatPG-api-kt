package helper

import helper.NullSafeMatchers.eq
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.eq

/**
 * This becomes very a very important feature that enables to adopt the presented testing strategy offered in PROCEDURE.md
 */
object NullSafeMatchers {

    const val STRING_TYPE  = "<String>"
    const val INTEGER_TYPE = 0
    const val LONG_TYPE    = 0L

    /**
     * I discovered this beautiful solution that fixes the problem born from the `null` returned by
     * [org.mockito.ArgumentMatchers] methods when non-primitives are passed as argument.
     * It satisfies both Mockito, in its need to assure that either all or none of the arguments use [org.mockito.ArgumentMatchers],
     * and Kotlin's null safety.
     *
     * @param this object to wrap with the [eq]
     * @return the exact object given
     */
    fun <O> (O).eq(): O {
        return eq(this) ?: this
    }

    fun <O> (O).any(): O {
        return ArgumentMatchers.any() ?: this
    }

}