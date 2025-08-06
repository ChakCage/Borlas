import React from 'react'
import {Post} from '../../api/postsApi'
import './PostCard.scss'

interface PostCardProps {
    post: Post;
    onEdit?: (post: Post) => void;
    onDelete?: (id: string) => void;
}

export const PostCard: React.FC<PostCardProps> = ({post, onEdit, onDelete}) => {
    const formatDate = (dateString: string) => {
        return new Date(dateString).toLocaleDateString('ru-RU', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        })
    }

    return (
        <div className="post-card">
            <div className="post-card__header">
                <h3 className="post-card__title">{post.title}</h3>
                <div className="post-card__actions">
                    {onEdit && (
                        <button
                            className="post-card__btn post-card__btn--edit"
                            onClick={() => onEdit(post)}
                        >
                            Редактировать
                        </button>
                    )}
                    {onDelete && (
                        <button
                            className="post-card__btn post-card__btn--delete"
                            onClick={() => onDelete(post.id)}
                        >
                            Удалить
                        </button>
                    )}
                </div>
            </div>

            <div className="post-card__content">
                <p>{post.content}</p>
            </div>

            <div className="post-card__footer">
        <span className="post-card__date">
          Создан: {formatDate(post.createdDate)}
        </span>
                {post.updatedDate !== post.createdDate && (
                    <span className="post-card__date">
            Обновлен: {formatDate(post.updatedDate)}
          </span>
                )}
            </div>
        </div>
    )
}
