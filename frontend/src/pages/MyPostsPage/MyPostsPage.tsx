import React, {useEffect, useState} from 'react'
import {useAppDispatch, useAppSelector} from '../../store/hooks'
import {fetchUserActivePosts, updatePost, deletePost, clearPosts} from '../../store/slices/postsSlice'
import {PostCardCustom} from '../../components/PostCardCustom/PostCardCustom'
import {PostForm} from '../../components/PostForm/PostForm'
import {Post, CreatePostRequest} from '../../api/postsApi'
import './MyPostsPage.scss'

export const MyPostsPage: React.FC = () => {
    const dispatch = useAppDispatch()
    const {posts, isLoading, error} = useAppSelector(state => state.posts)

    const [editingPost, setEditingPost] = useState<Post | null>(null)
    const [username, setUsername] = useState('testuser') // Можно сделать настраиваемым

    useEffect(() => {
        dispatch(clearPosts())
        dispatch(fetchUserActivePosts(username))
    }, [dispatch, username])

    const handleUpdatePost = async (postData: CreatePostRequest) => {
        if (editingPost) {
            await dispatch(updatePost({id: editingPost.id, postData}))
            setEditingPost(null)
        }
    }

    const handleDeletePost = async (id: string) => {
        if (window.confirm('Вы уверены, что хотите удалить этот пост?')) {
            await dispatch(deletePost(id))
        }
    }

    const handleEditPost = (post: Post) => {
        setEditingPost(post)
    }

    const handleCancelForm = () => {
        setEditingPost(null)
    }

    const handleUsernameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setUsername(e.target.value)
    }

    const handleLoadPosts = () => {
        if (username.trim()) {
            dispatch(clearPosts())
            dispatch(fetchUserActivePosts(username.trim()))
        }
    }

    if (isLoading && posts.length === 0) {
        return (
            <div className="my-posts-page">
                <div className="my-posts-page__loading">
                    <div className="spinner"></div>
                    <p>Загрузка постов...</p>
                </div>
            </div>
        )
    }

    return (
        <div className="my-posts-page">
            <div className="my-posts-page__header">
                <h1 className="my-posts-page__title">Мои посты</h1>
                <div className="my-posts-page__user-selector">
                    <input
                        type="text"
                        value={username}
                        onChange={handleUsernameChange}
                        placeholder="Введите имя пользователя"
                        className="my-posts-page__input"
                    />
                    <button
                        onClick={handleLoadPosts}
                        className="my-posts-page__btn"
                        disabled={!username.trim()}
                    >
                        Загрузить
                    </button>
                </div>
            </div>

            {error && (
                <div className="my-posts-page__error">
                    <p>{error}</p>
                </div>
            )}

            {editingPost && (
                <div className="my-posts-page__form-overlay">
                    <PostForm
                        post={editingPost}
                        onSubmit={handleUpdatePost}
                        onCancel={handleCancelForm}
                        isLoading={isLoading}
                    />
                </div>
            )}

            <div className="my-posts-page__content">
                {posts.length === 0 ? (
                    <div className="my-posts-page__empty">
                        <p>У пользователя "{username}" нет активных постов.</p>
                    </div>
                ) : (
                    <div className="my-posts-page__posts">
                        {posts.map(post => (
                            <PostCardCustom
                                key={post.id}
                                post={post}
                                onEdit={handleEditPost}
                                onDelete={handleDeletePost}
                            />
                        ))}
                    </div>
                )}
            </div>

            {isLoading && posts.length > 0 && (
                <div className="my-posts-page__loading-overlay">
                    <div className="spinner"></div>
                </div>
            )}
        </div>
    )
}
