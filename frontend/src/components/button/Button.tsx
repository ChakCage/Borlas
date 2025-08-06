import React from 'react'
import './Button.scss'

interface ButtonProps {
    text: string
    onClick: () => void
    isDisabled?: boolean
}

export const Button: React.FC<ButtonProps> = (props) => {

    const {isDisabled, text, onClick} = props

    return (
        <button className={"button-posts"}
                onClick={onClick}
                disabled={isDisabled}
        >{text}</button>)
}
