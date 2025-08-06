import React from 'react'
import {BrowserRouter as Router, Routes, Route} from 'react-router-dom'
import {Provider} from 'react-redux'
import {store} from './store'
import {Sidebar} from './components/Sidebar/Sidebar'
import {PostsPage} from './pages/PostsPage/PostsPage'
import {CreatePostPage} from './pages/CreatePostPage/CreatePostPage'
import {MyPostsPage} from './pages/MyPostsPage/MyPostsPage'
import {DeletedPostsPage} from './pages/DeletedPostsPage/DeletedPostsPage'
import {setAuthToken} from "./api/axios"
import './App.scss'
import {Button} from "./components/button/Button"

function App() {
    setAuthToken("testuser", "yourpassword")

    const onClickBtn = () => {
        // alert("Кнопка нажата")
        console.log("Кнопка нажата")
    }

    return (
        <Provider store={store}>
            <Router>
                <div className="App">
                    <Sidebar/>
                    <main className="App__main">
                        <Routes>
                            <Route path="/" element={<PostsPage/>}/>
                            <Route path="/posts" element={<PostsPage/>}/>
                            <Route path="/create" element={<CreatePostPage/>}/>
                            <Route path="/my-posts" element={<MyPostsPage/>}/>
                            <Route path="/deleted" element={<DeletedPostsPage/>}/>
                            <Route path="/testView" element={
                                <div style={{padding: "100px"}}>
                                    <Button text={"Button"} onClick={onClickBtn}/>
                                    <span>-----------</span>
                                    <Button text={"Button2"} onClick={onClickBtn} isDisabled={true}/>
                                </div>
                            }/>
                        </Routes>
                    </main>
                </div>
            </Router>
        </Provider>
    )
}

export default App
