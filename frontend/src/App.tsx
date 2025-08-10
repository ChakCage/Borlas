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
import {TextButton} from "./components/TextButton/TextButton"
import {ButtonTheme} from "./types/BtnThemeEnum"

function App() {
    setAuthToken("testuser", "yourpassword")

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
                                <div style={{padding: "100px", display: "flex", flexDirection: "column", gap: "15px"}}>
                                    <Button text={"Button Primary"} onClick={onClickBtn}/>
                                    <Button text={"Button Primary disabled"} onClick={onClickBtn} isDisabled={true}/>
                                    <Button text={"Button Soft"} onClick={onClickBtn} theme={ButtonTheme.Soft}/>
                                    <Button text={"Button Soft disabled"} onClick={onClickBtn} theme={ButtonTheme.Soft}
                                            isDisabled={true}/>
                                    <TextButton text={"Комментировать"} onClick={onClickTxtBtn}/>
                                    <TextButton text={"Комментировать"} onClick={onClickTxtBtn} isDisabled={true}/>
                                </div>}/>
                        </Routes>
                    </main>
                </div>
            </Router>
        </Provider>
    )
}

export default App
