import React, { useEffect, useMemo, useState } from 'react';
import { useAppDispatch, useAppSelector } from '../../store/hooks';
import { clearPosts, fetchUserDeletedPosts } from '../../store/slices/postsSlice';
import { PostCardCustom } from '../../components/PostCardCustom/PostCardCustom';
import { usersApi, User } from '../../api/usersApi';
import './DeletedPostsPage.scss';

export const DeletedPostsPage: React.FC = () => {
    const dispatch = useAppDispatch();
    const { posts, isLoading, error } = useAppSelector(s => s.posts);

    const [me, setMe] = useState<User | null>(null);
    const [loginQuery, setLoginQuery] = useState('');

    const isAdmin = useMemo(
        () => {
            const u = (me?.username ?? '').toLowerCase();
            return u === 'admin' || u === 'adminn';
        },
        [me]
    );

    useEffect(() => {
        (async () => {
            try {
                const resp = await usersApi.getMe();
                setMe(resp.data);
                setLoginQuery(resp.data.username);
                dispatch(clearPosts());
                dispatch(fetchUserDeletedPosts(resp.data.username));
            } catch {
                // error уже в слайсе
            }
        })();
    }, [dispatch]);

    const handleLoadPosts = () => {
        const login = (isAdmin ? loginQuery : me?.username || '').trim();
        if (!login) return;
        dispatch(clearPosts());
        dispatch(fetchUserDeletedPosts(login));
    };

    return (
        <div className="deleted-posts-page">
            <div className="deleted-posts-page__header">
                <h1 className="deleted-posts-page__title">Удалённые посты</h1>

                <form
                    className="deleted-posts-page__user-selector"
                    onSubmit={(e) => {
                        e.preventDefault();
                        handleLoadPosts();
                    }}
                >
                    <input
                        type="text"
                        value={isAdmin ? loginQuery : (me?.username ?? '')}
                        onChange={(e) => isAdmin && setLoginQuery(e.target.value)}
                        placeholder="username"
                        className="deleted-posts-page__input"
                        readOnly={!isAdmin}
                    />
                    <button
                        type="submit"
                        className="deleted-posts-page__btn"
                        disabled={
                            isLoading ||
                            !(isAdmin ? loginQuery.trim() : (me?.username ?? '').trim())
                        }
                    >
                        Загрузить
                    </button>
                </form>
            </div>

            {error && (
                <div className="deleted-posts-page__error">
                    <p>{error}</p>
                </div>
            )}

            <div className="deleted-posts-page__content">
                {isLoading && posts.length === 0 ? (
                    <div className="deleted-posts-page__loading">
                        <div className="spinner" />
                        <p>Загрузка удалённых постов…</p>
                    </div>
                ) : posts.length === 0 ? (
                    <div className="deleted-posts-page__empty">
                        <p>У пользователя "{isAdmin ? loginQuery : me?.username}" нет удалённых постов.</p>
                    </div>
                ) : (
                    <div className="deleted-posts-page__posts">
                        {posts.map((p) => (
                            <PostCardCustom key={p.id} post={p} />
                        ))}
                    </div>
                )}
            </div>

            {isLoading && posts.length > 0 && (
                <div className="deleted-posts-page__loading-overlay">
                    <div className="spinner" />
                </div>
            )}
        </div>
    );
};

export default DeletedPostsPage;
