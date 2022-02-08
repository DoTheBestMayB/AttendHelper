package com.github.dodobest.domain

class JavaScriptExpressionHandler {
    fun makeJavascriptSentence(sentence: String, needFormatSetting: Boolean) : String {
        return if (needFormatSetting) {
            "javascript:(function()'{'$sentence'}')()"
        } else {
            "javascript:(function(){$sentence})()"
        }
    }

    fun makeClickElementByIdSentence(elementId: String) : String {
        var sentence = "l={$elementId}const e=new Event(''click'');l.dispatchEvent(e);"
        sentence = makeJavascriptSentence(sentence, true)

        return sentence
    }

    fun makeFindElementByXpathSentence(path: String): String {
        return "document.evaluate(''{$path}'', document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;"
    }
}