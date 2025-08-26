// src/App.tsx
import React from 'react'
import {BrowserRouter as Router, Navigate, Route, Routes} from 'react-router-dom'
import {Provider} from 'react-redux'
import {store} from './store'
import {Sidebar} from './components/Sidebar/Sidebar'
import {PostsPage} from './pages/PostsPage/PostsPage'
import {CreatePostPage} from './pages/CreatePostPage/CreatePostPage'
import MyPostsPage from './pages/MyPostsPage/MyPostsPage'
import DeletedPostsPage from './pages/DeletedPostsPage/DeletedPostsPage'
// ❌ УДАЛИ это: import {setAuthToken} from "./api/axios"
import AuthModal from './pages/AuthModal/AuthModal'
import { AuthProvider } from './context/AuthContext'
import ProtectedRoute from './components/ProtectedRoute/ProtectedRoute'
import MyProfilePage from './pages/MyProfilePage/MyProfilePage'   // ✅ ДОБАВЬ
import './App.scss'

function App() {
    return (
        <Provider store={store}>
            <AuthProvider>
                <Router>
                    <div className="App">
                        <Sidebar/>
                        <main className="App__main">
                            <Routes>
                                {/* Публично */}
                                <Route path="/login" element={<AuthModal/>}/>
                                {/* Защищённо */}
                                <Route path="/" element={<ProtectedRoute><PostsPage/></ProtectedRoute>}/>
                                <Route path="/posts" element={<ProtectedRoute><PostsPage/></ProtectedRoute>}/>
                                <Route path="/create" element={<ProtectedRoute><CreatePostPage/></ProtectedRoute>}/>
                                <Route path="/my-posts" element={<ProtectedRoute><MyPostsPage/></ProtectedRoute>}/>
                                <Route path="/deleted" element={<ProtectedRoute><DeletedPostsPage/></ProtectedRoute>}/>
                                <Route path="/profile" element={<ProtectedRoute><MyProfilePage/></ProtectedRoute>}/> {/* ✅ */}
                                {/* Фоллбек */}
                                <Route path="*" element={<Navigate to="/login" replace/>}/>
                            </Routes>
                        </main>
                    </div>
                </Router>
            </AuthProvider>
        </Provider>
    )
}

export default App
