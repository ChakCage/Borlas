import React from 'react';
import { Post } from '../../api/postsApi';
import './PostCardCustom.scss';
import { formatDate } from '../../utils/dateUtil';
import { TextButton } from '../TextButton/TextButton';
import { Button } from '../button/Button';

interface PostCardProps {
    post: Post;
    onEdit?: (post: Post) => void;
    onDelete?: (id: string) => void;
}

export const PostCardCustom: React.FC<PostCardProps> = ({ post, onEdit, onDelete }) => {
    // гарантируем, что что-то покажем даже если поле пустое
    const author = (post as any).authorUsername || '—';
    const date = post.updatedDate ? formatDate(post.updatedDate) : formatDate(post.createdDate);

    return (
        <div className="post-card2">
            <div className="post-card2__header">
                <h3 className="post-card2__title">{post.title}</h3>

                {/* справа: точка, дата и автор */}
                <div className="post-card2__meta">
                    <span className="post-card2__dot" />
                    <span className="post-card2__date">{date}</span>
                    <span className="post-card2__author">{author}</span>
                </div>
            </div>

            <div className="post-card2__content">
                <p>{post.content}</p>
            </div>

            <div className="post-card2__footer">
                <div className="post-card2__actions">
                    {onEdit && <TextButton text="Редактировать" onClick={() => onEdit(post)} />}
                    {onDelete && <Button text="Удалить" onClick={() => onDelete(post.id)} />}
                </div>
            </div>
        </div>
    );
};

export default PostCardCustom;
