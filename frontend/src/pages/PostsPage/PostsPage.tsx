import React, {useEffect, useState} from 'react'
import {useAppDispatch, useAppSelector} from '../../store/hooks'
import {fetchPosts, updatePost, deletePost} from '../../store/slices/postsSlice'
import {PostCardCustom} from '../../components/PostCardCustom/PostCardCustom' //временно поменял на 2
import {PostForm} from '../../components/PostForm/PostForm'
import {Post, CreatePostRequest} from '../../api/postsApi'
import './PostsPage.scss'

export const PostsPage: React.FC = () => {
    const dispatch = useAppDispatch()
    const {posts, isLoading, error} = useAppSelector(state => state.posts)

    const [editingPost, setEditingPost] = useState<Post | null>(null)

    useEffect(() => {
        dispatch(fetchPosts())
    }, [dispatch])

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

    if (isLoading && posts.length === 0) {
        return (
            <div className="posts-page">
                <div className="posts-page__loading">
                    <div className="spinner"></div>
                    <p>Загрузка постов...</p>
                </div>
            </div>
        )
    }

    return (
        <div className="posts-page">
            <div className="posts-page__header">
                <h1 className="posts-page__title">Все посты</h1>
            </div>

            {error && (
                <div className="posts-page__error">
                    <p>{error}</p>
                </div>
            )}

            {editingPost && (
                <div className="posts-page__form-overlay">
                    <PostForm
                        post={editingPost}
                        onSubmit={handleUpdatePost}
                        onCancel={handleCancelForm}
                        isLoading={isLoading}
                    />
                </div>
            )}

            <div className="posts-page__content">
                {posts.length === 0 ? (
                    <div className="posts-page__empty">
                        <p>Постов пока нет. Создайте первый пост!</p>
                    </div>
                ) : (
                    <div className="posts-page__posts">
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
                <div className="posts-page__loading-overlay">
                    <div className="spinner"></div>
                </div>
            )}
        </div>
    )
}
