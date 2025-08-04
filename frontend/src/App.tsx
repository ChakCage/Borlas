import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Provider } from 'react-redux';
import { store } from './store';
import { Sidebar } from './components/Sidebar/Sidebar';
import { PostsPage } from './pages/PostsPage/PostsPage';
import { CreatePostPage } from './pages/CreatePostPage/CreatePostPage';
import { MyPostsPage } from './pages/MyPostsPage/MyPostsPage';
import { DeletedPostsPage } from './pages/DeletedPostsPage/DeletedPostsPage';
import {setAuthToken} from "./api/axios"
import './App.scss';

function App() {
  setAuthToken("testuser" ,"yourpassword")
  return (
    <Provider store={store}>
      <Router>
        <div className="App">
          <Sidebar />
          <main className="App__main">
            <Routes>
              <Route path="/" element={<PostsPage />} />
              <Route path="/posts" element={<PostsPage />} />
              <Route path="/create" element={<CreatePostPage />} />
              <Route path="/my-posts" element={<MyPostsPage />} />
              <Route path="/deleted" element={<DeletedPostsPage />} />
            </Routes>
          </main>
        </div>
      </Router>
    </Provider>
  );
}

export default App;
