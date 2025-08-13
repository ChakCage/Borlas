import React from 'react'
import {Post} from '../../api/postsApi'
import './PostCard2.scss'

interface PostCardProps {
    post: Post;
    onEdit?: (post: Post) => void;
    onDelete?: (id: string) => void;
}

export const PostCard: React.FC<PostCardProps> = ({post, onEdit, onDelete}) => {
    const formatDate = (dateString: string) => {
        return new Date(dateString).toLocaleDateString('ru-RU', {
            year: 'numeric',
            month: 'numeric',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        })
    }

    return (
        <div className="post-card2">
            <div className="post-card2__header">
                <h3 className="post-card2__title">{post.title}</h3>
                <span className="post-card2__avatar"></span>
                <div className="post-card2__container">
                    <span className="post-card2__author">{post.authorId}</span>
                    <span className="post-card2__date">{post.updatedDate ? formatDate(post.updatedDate) : formatDate(post.createdDate)}</span>
                </div>
            </div>

            <div className="post-card2__content">
                <p>{post.content}</p>
            </div>
            <div className="post-card2__comments">
                <p></p>
            </div>
            <div className="post-card2__footer">
                <div className="post-card2__actions">
                    <button
                        className="post-card2__btn post-card2__btn--comment"
                        >
                        Комментировать
                    </button>
                    {onEdit && (
                        <button
                            className="post-card2__btn post-card2__btn--edit"
                            onClick={() => onEdit(post)}
                        >
                            Редактировать
                        </button>
                    )}
                    {onDelete && (
                        <button
                            className="post-card2__btn post-card2__btn--delete"
                            onClick={() => onDelete(post.id)}
                        >
                            Удалить
                        </button>
                    )}
                </div>
            </div>
        </div>
    )
}
