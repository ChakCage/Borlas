import React, { useEffect, useMemo, useState } from 'react';
import { useAppDispatch, useAppSelector } from '../../store/hooks';
import { fetchUserActivePosts, updatePost, deletePost, clearPosts } from '../../store/slices/postsSlice';
import { PostCardCustom } from '../../components/PostCardCustom/PostCardCustom';
import { PostForm } from '../../components/PostForm/PostForm';
import { Post, CreatePostRequest } from '../../api/postsApi';
import { usersApi, User } from '../../api/usersApi';
import './MyPostsPage.scss';

export const MyPostsPage: React.FC = () => {
    const dispatch = useAppDispatch();
    const { posts, isLoading, error } = useAppSelector(s => s.posts);

    const [editingPost, setEditingPost] = useState<Post | null>(null);
    const [me, setMe] = useState<User | null>(null);
    const [loginQuery, setLoginQuery] = useState('');

    const isAdmin = useMemo(
        () => (me?.username ?? '').toLowerCase() === 'admin' || (me?.username ?? '').toLowerCase() === 'adminn',
        [me]
    );

    useEffect(() => {
        (async () => {
            try {
                const resp = await usersApi.getMe();
                setMe(resp.data);
                setLoginQuery(resp.data.username);
                dispatch(clearPosts());
                dispatch(fetchUserActivePosts(resp.data.username));
            } catch {}
        })();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const handleLoadPosts = () => {
        const login = (isAdmin ? loginQuery : me?.username || '').trim();
        if (!login) return;
        dispatch(clearPosts());
        dispatch(fetchUserActivePosts(login));
    };

    const handleUpdatePost = async (postData: CreatePostRequest) => {
        if (editingPost) {
            await dispatch(updatePost({ id: editingPost.id, postData }));
            setEditingPost(null);
        }
    };

    const handleDeletePost = async (id: string) => {
        if (window.confirm('Вы уверены, что хотите удалить этот пост?')) {
            await dispatch(deletePost(id));
        }
    };

    return (
        <div className="my-posts-page">
            <div className="my-posts-page__header">
                <h1 className="my-posts-page__title">Мои посты</h1>

                <form
                    className="my-posts-page__user-selector"
                    onSubmit={(e) => { e.preventDefault(); handleLoadPosts(); }}
                >
                    <input
                        type="text"
                        value={isAdmin ? loginQuery : me?.username ?? ''}
                        onChange={(e) => isAdmin && setLoginQuery(e.target.value)}
                        placeholder="username"
                        className="my-posts-page__input"
                        readOnly={!isAdmin}
                    />
                    <button
                        type="submit"
                        className="my-posts-page__btn"
                        disabled={isLoading || !(isAdmin ? loginQuery.trim() : (me?.username ?? '').trim())}
                    >
                        Загрузить
                    </button>
                </form>
            </div>

            {error && <div className="my-posts-page__error"><p>{error}</p></div>}

            {editingPost && (
                <div className="my-posts-page__form-overlay">
                    <PostForm
                        post={editingPost}
                        onSubmit={handleUpdatePost}
                        onCancel={() => setEditingPost(null)}
                        isLoading={isLoading}
                    />
                </div>
            )}

            <div className="my-posts-page__content">
                {isLoading && posts.length === 0 ? (
                    <div className="my-posts-page__loading">
                        <div className="spinner" /> <p>Загрузка постов…</p>
                    </div>
                ) : posts.length === 0 ? (
                    <div className="my-posts-page__empty">
                        <p>У пользователя "{isAdmin ? loginQuery : me?.username}" нет активных постов.</p>
                    </div>
                ) : (
                    <div className="my-posts-page__posts">
                        {posts.map(p => (
                            <PostCardCustom key={p.id} post={p} onEdit={setEditingPost} onDelete={handleDeletePost} />
                        ))}
                    </div>
                )}
            </div>

            {isLoading && posts.length > 0 && (
                <div className="my-posts-page__loading-overlay"><div className="spinner" /></div>
            )}
        </div>
    );
};

export default MyPostsPage;
