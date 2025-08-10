// стиль в название ключа и логика в его значение
// spreadStyles({['element_hide']: true, ['element_active']: false и т.п.})
// возвращает строку стилей через пробел с одним пробелом в Конце
export const spreadStyles = ( object: { [key: string]: boolean } ): string => {
    let stringToReturn = ''
    for (const key in object) {
        if (object.hasOwnProperty(key) && object[key]) {
            stringToReturn += `${ key } `
        }
    }
    return stringToReturn
}

// через запятую указываем стили splitStyles('element_hide','element_active' и т.п.)
// возвращает: строку стилей через пробел с одним пробелом в Начале
export const splitStyles = ( ...stylesToSplit: string[] ): string =>
    stylesToSplit.reduce(( styles, value ) => `${styles} ${value}`,'')
