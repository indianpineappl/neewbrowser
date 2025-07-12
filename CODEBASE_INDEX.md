# NeewBrowser Codebase Index

This document provides an overview of the main modules in the NeewBrowser application, their responsibilities, and key interactions.

## Main Modules

### `app/src/main/java/com/neew/browser/MainActivity.java`
- **Responsibility:** The main entry point and primary screen of the application. It hosts the `GeckoView` for web browsing, manages browser sessions (tabs), and handles all user interactions with the browser UI.
- **Key Features:**
    - Manages the lifecycle of `GeckoSession` objects.
    - Handles UI elements like the URL bar, navigation buttons (back, forward, refresh), and the settings menu.
    - Implements TV-specific navigation, including the custom `TvCursorView` for D-pad control.
    - Manages browser features such as desktop mode, cookie settings, and JavaScript execution.
    - Handles downloads, history, and tab switching intents.
    - Contains the core logic for updating the UI based on web page loading state (`onLocationChange`, `onProgressChange`).

### `app/src/main/java/com/neew/browser/TabSwitcherActivity.java`
- **Responsibility:** Provides a UI for users to view, switch between, and close open tabs. It displays a grid of tab snapshots.
- **Key Features:**
    - Displays a `RecyclerView` of all open tabs using `TabAdapter`.
    - Handles user input for selecting, opening, and closing tabs.
    - Manages focus and navigation for TV devices using a D-pad.
    - Communicates back to `MainActivity` which tab was selected.

### `app/src/main/java/com/neew/browser/TabAdapter.java`
- **Responsibility:** An adapter for the `RecyclerView` in `TabSwitcherActivity`. It binds tab data (snapshot, title) to the view for each item in the tab list.
- **Key Features:**
    - Inflates the layout for each tab item.
    - Loads and displays a snapshot of the web page for each tab.
    - Handles click and focus events on individual tab items, enabling tab switching and closing.

### `app/src/main/java/com/neew/browser/DownloadsActivity.java`
- **Responsibility:** Displays a list of downloaded files. Users can view, open, or delete downloads from this screen.
- **Key Features:**
    - Lists all files downloaded through the browser.
    - Allows users to open downloaded files using appropriate external applications.
    - Provides functionality to delete downloaded files and remove them from the list.
    - Persists the download list using `SharedPreferences`.

### `app/src/main/java/com/neew/browser/TvCursorView.java`
- **Responsibility:** A custom `View` that implements a cursor for TV navigation. This is essential for interacting with web content on Android TV devices that lack a touchscreen.
- **Key Features:**
    - Renders a custom cursor on the screen.
    - Moves the cursor based on D-pad input.
    - Simulates clicks and interacts with web page elements.
    - Shows a progress indicator during page loads.

## Data and Event Flows

### 1. Page Navigation and Loading

This flow describes what happens when a user enters a URL or clicks a link.

1.  **User Action:** The user enters a URL in the `UrlBar` in `MainActivity` or clicks a link within the `GeckoView`.
2.  **Load Request:** `MainActivity` calls `getActiveSession().load(new GeckoSession.Loader.UriRequest(uri, ...))` to initiate page loading.
3.  **GeckoView Callbacks:** As the page loads, `GeckoView` triggers several callbacks on its `NavigationDelegate` (implemented by `MainActivity`).
    *   `onLocationChange(session, url)`: Fired when the URL of the page changes. `MainActivity` uses this to update its internal state (`sessionUrlMap`) and trigger a UI update via `updateUIForActiveSession()`.
    *   `onProgressChange(session, progress)`: Fired periodically during page load. `MainActivity` updates the progress bar in the UI.
    *   `onSecurityChange(session, state)`: Fired when the security status of the page changes (e.g., HTTPS). `MainActivity` updates security indicators in the UI.
4.  **UI Update:** The `updateUIForActiveSession()` method is called to refresh the URL bar, navigation buttons (back/forward), and other UI elements. This method is guarded by the `isUiReady` flag to prevent updates before the UI is fully initialized.

### 2. Tab Switching

This flow describes how the app switches between active tabs.

1.  **User Action:** The user opens the `TabSwitcherActivity` from `MainActivity`.
2.  **Display Tabs:** `TabSwitcherActivity` displays a grid of open tabs using `TabAdapter`. Each tab is represented by a snapshot.
3.  **Select Tab:** The user clicks on a tab. The `TabAdapter`'s `onClickListener` captures the position of the selected tab.
4.  **Return to Main:** `TabSwitcherActivity` finishes and returns the selected tab's index to `MainActivity` via an `Intent` result.
5.  **Activate Tab:** In `MainActivity`'s `activityResultLauncher`, the `switchToTab(index)` method is called.
6.  **Session Switch:** `switchToTab` sets the new active `GeckoSession` and attaches it to the `GeckoView`.
7.  **Final UI Update:** `switchToTab` calls `updateUIForActiveSession()` to ensure the URL bar and all other UI components reflect the state of the newly activated tab.
