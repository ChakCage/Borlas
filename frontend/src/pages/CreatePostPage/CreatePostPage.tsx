import React from 'react'
import {useNavigate} from 'react-router-dom'
import {useAppDispatch, useAppSelector} from '../../store/hooks'
import {createPost} from '../../store/slices/postsSlice'
import {PostForm} from '../../components/PostForm/PostForm'
import {CreatePostRequest} from '../../api/postsApi'
import './CreatePostPage.scss'

export const CreatePostPage: React.FC = () => {
    const dispatch = useAppDispatch()
    const navigate = useNavigate()
    const {isLoading, error} = useAppSelector(state => state.posts)

    const handleCreatePost = async (postData: CreatePostRequest) => {
        const result = await dispatch(createPost(postData))
        if (createPost.fulfilled.match(result)) {
            navigate('/')
        }
    }

    const handleCancel = () => {
        navigate('/')
    }

    return (
        <div className="create-post-page">
            <div className="create-post-page__container">
                <div className="create-post-page__header">
                    <h1 className="create-post-page__title">Создать новый пост</h1>
                    <button
                        className="create-post-page__back-btn"
                        onClick={handleCancel}
                    >
                        ← Назад к постам
                    </button>
                </div>

                {error && (
                    <div className="create-post-page__error">
                        <p>{error}</p>
                    </div>
                )}

                <div className="create-post-page__content">
                    <PostForm
                        onSubmit={handleCreatePost}
                        onCancel={handleCancel}
                        isLoading={isLoading}
                    />
                </div>
            </div>
        </div>
    )
}
