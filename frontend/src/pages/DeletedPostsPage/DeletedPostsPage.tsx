import React, { useEffect, useState } from 'react';
import { useAppDispatch, useAppSelector } from '../../store/hooks';
import { fetchUserDeletedPosts, clearPosts } from '../../store/slices/postsSlice';
import { PostCard } from '../../components/PostCard/PostCard';
import { Post } from '../../api/postsApi';
import './DeletedPostsPage.scss';

export const DeletedPostsPage: React.FC = () => {
  const dispatch = useAppDispatch();
  const { posts, loading, error } = useAppSelector(state => state.posts);
  
  const [username, setUsername] = useState('testuser');

  useEffect(() => {
    dispatch(clearPosts());
    dispatch(fetchUserDeletedPosts(username));
  }, [dispatch, username]);

  const handleUsernameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setUsername(e.target.value);
  };

  const handleLoadPosts = () => {
    if (username.trim()) {
      dispatch(clearPosts());
      dispatch(fetchUserDeletedPosts(username.trim()));
    }
  };

  if (loading && posts.length === 0) {
    return (
      <div className="deleted-posts-page">
        <div className="deleted-posts-page__loading">
          <div className="spinner"></div>
          <p>Загрузка удаленных постов...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="deleted-posts-page">
      <div className="deleted-posts-page__header">
        <h1 className="deleted-posts-page__title">Удаленные посты</h1>
        <div className="deleted-posts-page__user-selector">
          <input
            type="text"
            value={username}
            onChange={handleUsernameChange}
            placeholder="Введите имя пользователя"
            className="deleted-posts-page__input"
          />
          <button
            onClick={handleLoadPosts}
            className="deleted-posts-page__btn"
            disabled={!username.trim()}
          >
            Загрузить
          </button>
        </div>
      </div>

      {error && (
        <div className="deleted-posts-page__error">
          <p>{error}</p>
        </div>
      )}

      <div className="deleted-posts-page__content">
        {posts.length === 0 ? (
          <div className="deleted-posts-page__empty">
            <p>У пользователя "{username}" нет удаленных постов.</p>
          </div>
        ) : (
          <div className="deleted-posts-page__posts">
            {posts.map(post => (
              <PostCard
                key={post.id}
                post={post}
                // Удаленные посты нельзя редактировать или удалять
              />
            ))}
          </div>
        )}
      </div>

      {loading && posts.length > 0 && (
        <div className="deleted-posts-page__loading-overlay">
          <div className="spinner"></div>
        </div>
      )}
    </div>
  );
}; 