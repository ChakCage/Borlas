import React from 'react';
import { Post } from '../../api/postsApi';
import './PostCard.scss';
import { formatDate } from '../../utils/dateUtil';

interface PostCardProps {
    post: Post;
    onEdit?: (post: Post) => void;
    onDelete?: (id: string) => void;
}

export const PostCard: React.FC<PostCardProps> = ({ post, onEdit, onDelete }) => {
    const topDate = post.updatedDate || post.createdDate;

    return (
        <div className="post-card">
            {/* заголовок + автор справа сверху */}
            <div className="post-card__header">
                <h3 className="post-card__title">{post.title}</h3>
                <div className="post-card__meta">
                    <span className="post-card__avatar" />
                    <span className="post-card__author">@{post.authorUsername}</span>
                    <span className="post-card__dot">•</span>
                    <span className="post-card__date">{formatDate(topDate)}</span>
                </div>
            </div>

            <div className="post-card__content">
                <p>{post.content}</p>
            </div>

            <div className="post-card__footer">
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

                <div className="post-card__dates">
                    <span className="post-card__date">Создан: {formatDate(post.createdDate)}</span>
                    {post.updatedDate !== post.createdDate && (
                        <span className="post-card__date">Обновлён: {formatDate(post.updatedDate)}</span>
                    )}
                </div>
            </div>
        </div>
    );
};

export default PostCard;
