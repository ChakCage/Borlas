import React, {useState, useEffect} from 'react'
import {Post, CreatePostRequest} from '../../api/postsApi'
import './PostForm.scss'

interface PostFormProps {
    post?: Post;
    onSubmit: (data: CreatePostRequest) => void;
    onCancel: () => void;
    isLoading?: boolean;
}

export const PostForm: React.FC<PostFormProps> = ({
                                                      post,
                                                      onSubmit,
                                                      onCancel,
                                                      isLoading = false
                                                  }) => {
    const [title, setTitle] = useState('')
    const [content, setContent] = useState('')

    useEffect(() => {
        if (post) {
            setTitle(post.title)
            setContent(post.content)
        }
    }, [post])

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault()
        if (title.trim() && content.trim()) {
            onSubmit({title: title.trim(), content: content.trim()})
        }
    }

    return (
        <div className="post-form">
            <h2 className="post-form__title">
                {post ? 'Редактировать пост' : 'Создать новый пост'}
            </h2>

            <form onSubmit={handleSubmit} className="post-form__form">
                <div className="post-form__field">
                    <label htmlFor="title" className="post-form__label">
                        Заголовок
                    </label>
                    <input
                        id="title"
                        type="text"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        className="post-form__input"
                        placeholder="Введите заголовок поста"
                        required
                        disabled={isLoading}
                    />
                </div>

                <div className="post-form__field">
                    <label htmlFor="content" className="post-form__label">
                        Содержание
                    </label>
                    <textarea
                        id="content"
                        value={content}
                        onChange={(e) => setContent(e.target.value)}
                        className="post-form__textarea"
                        placeholder="Введите содержание поста"
                        rows={6}
                        required
                        disabled={isLoading}
                    />
                </div>

                <div className="post-form__actions">
                    <button
                        type="button"
                        onClick={onCancel}
                        className="post-form__btn post-form__btn--cancel"
                        disabled={isLoading}
                    >
                        Отмена
                    </button>
                    <button
                        type="submit"
                        className="post-form__btn post-form__btn--submit"
                        disabled={isLoading || !title.trim() || !content.trim()}
                    >
                        {isLoading ? 'Сохранение...' : (post ? 'Обновить' : 'Создать')}
                    </button>
                </div>
            </form>
        </div>
    )
}
