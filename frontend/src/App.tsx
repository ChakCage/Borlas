import React from 'react'
import {BrowserRouter as Router, Navigate, Route, Routes} from 'react-router-dom'
import {Provider} from 'react-redux'
import {store} from './store'
import {Sidebar} from './components/Sidebar/Sidebar'
import {PostsPage} from './pages/PostsPage/PostsPage'
import {CreatePostPage} from './pages/CreatePostPage/CreatePostPage'
import {MyPostsPage} from './pages/MyPostsPage/MyPostsPage'
import {DeletedPostsPage} from './pages/DeletedPostsPage/DeletedPostsPage'
import {setAuthToken} from "./api/axios"
import AuthModal from './pages/AuthModal/AuthModal'
import { AuthProvider } from './context/AuthContext'
import ProtectedRoute from './components/ProtectedRoute/ProtectedRoute'
import './App.scss'

// import {Button} from "./components/button/Button"
// import {TextButton} from "./components/TextButton/TextButton"
// import {ButtonTheme, TextButtonTheme} from "./types/BtnThemeEnum"
// import ProfilePage from "./pages/MyProfilePage/MyProfilePage";
// import AuthModal from "./pages/AuthModal/AuthModal";

function App() {
    setAuthToken("adminn", "adminn")

    const onClickBtn = () => {
        // alert("Кнопка нажата")
        console.log("Кнопка нажата")
    }

    const onClickTxtBtn = () => {
        // alert("Кнопка коммента нажата")
        console.log("Кнопка коммента нажата")
    }

    return (
        <Provider store={store}>
            <AuthProvider>
                <Router>
                    <div className="App">
                        <Sidebar/>
                        <main className="App__main">
                            <Routes>
                                {/* Публичные маршруты */}
                                <Route path="/login" element={<AuthModal/>}/>
                                {/* Защищенные маршруты */}
                                <Route path="/" element={<ProtectedRoute><PostsPage/></ProtectedRoute>}/>
                                <Route path="/posts" element={<ProtectedRoute><PostsPage/></ProtectedRoute>}/>
                                <Route path="/create" element={<ProtectedRoute><CreatePostPage/></ProtectedRoute>}/>
                                <Route path="/my-posts" element={<ProtectedRoute><MyPostsPage/></ProtectedRoute>}/>
                                <Route path="/deleted" element={<ProtectedRoute><DeletedPostsPage/></ProtectedRoute>}/>
                                {/* Перенаправление для неавторизованных */}
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
