# Agrupaê — Frontend 🚀

This is the repository for the frontend of **Agrupaê**, a web platform designed to facilitate the creation, management, and tracking of group projects within academic classes.

The frontend is built using **React**, **TypeScript**, and **Vite**, adhering to the **Bulletproof React** architecture pattern (feature-based organization).

---

## 🛠️ Tech Stack & Dependencies

The primary libraries and tools used in this project include:

*   **Vite**: Fast bundler and development server.
*   **React 19**: Base library for building user interfaces.
*   **TypeScript**: JavaScript superset that adds static type definitions to the codebase.
*   **React Router DOM**: Client-side routing and SPA navigation.
*   **Axios**: HTTP client for communicating with the backend (equipped with interceptors for authorization headers injection and silent token refreshing).
*   **React Query (TanStack)**: Async state management, data caching, and server state synchronization.
*   **React Hook Form & Zod**: Form handling coupled with robust, type-safe schema validations.
*   **Zustand**: Simple global state management (specifically powering the *Toast* notification system).
*   **CSS Modules**: Component-level style scoping to avoid conflicts.
*   **Husky & Lint-Staged**: Git hooks triggered before each commit to enforce formatting and linting.

---

## 🚀 Getting Started (Local Development)

### Prerequisites
*   **Node.js** (version 18 or higher recommended)
*   **npm** (comes bundled with Node.js)
*   Agrupaê Backend server running (by default on port `8081`)

### Step 1: Install Dependencies
Open your terminal in the frontend directory and run:
```bash
npm install
```

### Step 2: Start the Development Server
To launch the Vite development server locally:
```bash
npm run dev
```
The application will be accessible at: [http://localhost:5173](http://localhost:5173).

> 💡 **Vite Proxy:** The development server is configured (`vite.config.ts`) to forward API requests (such as `/auth`, `/users`, `/courses`) automatically to the backend server running locally (default: `http://localhost:8081`).

---

## 🏗️ Folder Structure (Bulletproof)

The project layout organizes files by **Features**, grouping all related logic under a single directory for that specific domain:

```
src/
├── app/                    # Global app configurations
│   ├── providers/          # Global context providers (Auth, QueryClient)
│   └── router/             # Router definition and route protection
├── components/             # Reusable global UI components
│   └── ui/                 # Toast, LoadingScreen, NotFoundPage, UserAvatar
├── features/               # Feature modules
│   ├── auth/               # Login, Sign Up, and User Profile
│   ├── classes/            # Class creation, search, and list actions
│   ├── assignments/        # Homework and group project assignments management
│   └── group/              # Group creation, joining, and management logic
├── lib/                    # Library configurations (Axios, translation dictionary)
├── types/                  # Shareable global type definitions
└── utils/                  # Helper utilities
```

Inside each folder in `features/`, code is divided into standard layers:
*   `api/`: Pure API fetch/mutations using Axios.
*   `hooks/`: Custom React hooks connecting APIs to React Query states.
*   `pages/`: Full views rendered by the router.
*   `types/`: Domain-specific TypeScript interfaces.
*   `components/`: Local components used exclusively within this feature (e.g., `ClassCard`).

---

## ⚙️ Environment Variables

If you need to configure the backend API target for development or production, adjust the following variable:

| Variable | Description | Default Value |
|---|---|---|
| `VITE_API_URL` | The target backend server address | `http://localhost:8081` |

---

## 🧹 Code Quality & Linting

The repository uses Husky to enforce standard formatting before commits:
*   **Prettier:** Handles code formatting rules.
*   **ESLint:** Runs static analysis to prevent bad practices.

Available scripts:
*   `npm run lint` — Performs ESLint checking across source files.
*   `npm run build` — Compiles TypeScript and outputs production-ready HTML/JS/CSS bundles in the `/dist` directory.
