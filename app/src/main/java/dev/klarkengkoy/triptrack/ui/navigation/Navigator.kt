package dev.klarkengkoy.triptrack.ui.navigation

import androidx.navigation3.runtime.NavKey

/**
 * Handles navigation events (forward and back) by updating the navigation state.
 */
class Navigator(val state: NavigationState){
    fun navigate(route: NavKey){
        if (route in state.backStacks.keys){
            // This is a top level route, just switch to it.
            state.topLevelRoute = route
        } else {
            state.backStacks[state.topLevelRoute]?.add(route)
        }
    }

    fun goBack(){
        val currentStack = state.backStacks[state.topLevelRoute] ?:
        error("Stack for ${state.topLevelRoute} not found")
        val currentRoute = currentStack.last()

        // If we're at the base of the current route, go back to the start route stack.
        if (currentRoute == state.topLevelRoute){
            state.topLevelRoute = state.startRoute
        } else {
            currentStack.removeLastOrNull() // This works as it is an extension on MutableList
        }
    }

    fun backToRoot() {
        val currentStack = state.backStacks[state.topLevelRoute] ?: return
        // The crash happened here because NavBackStack might not directly support removeLast()
        // or it's a different type than expected. NavBackStack implements List, but not MutableList directly in all versions or wrappers.
        // However, based on the crash: java.lang.NoSuchMethodError: No virtual method removeLast()
        // It seems we are calling removeLast on something that doesn't have it at runtime.
        
        // Let's assume backStacks value is a MutableList or similar which supports standard list operations.
        // But looking at NavigationState.kt, backStacks values are created via rememberNavBackStack(key).
        // rememberNavBackStack returns a NavBackStack.
        
        // Workaround: Use remove(currentStack.last()) or similar index-based removal if removeLast is missing.
        // Or simply repeatedly call remove at the last index.
        
        while (currentStack.size > 1) {
             currentStack.removeAt(currentStack.lastIndex)
        }
    }
}
