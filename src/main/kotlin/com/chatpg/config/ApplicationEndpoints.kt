package com.chatpg.config

object ApplicationEndpoints {

    object Auth {
        const val BASE = "/auth"

        const val LOGIN = "/login"

        const val FULFILL_A2F = "/fulfill-a2f"
        const val REFRESH_TOKENS = "/refresh-tokens"
        const val LOGIN_WITH_HANDLE = "/login-with-handle"
        const val OPEN_PASSWORD_CHANGE = "/open-password-change"
        const val FULFILL_PASSWORD_CHANGE = "/fulfill-password-change"
        const val OPEN_ACCOUNT_CREATION = "/open-account-creation"
        const val FULFILL_ACCOUNT_CREATION = "/fulfill-account-creation"
    }

    object ExternalLogin {
        const val BASE = "${Auth.BASE}${Auth.LOGIN}/with"

        const val GITHUB = "$BASE/github"
        const val GOOGLE = "$BASE/google"
    }

    object AuthSecure {
        const val BASE = "${Auth.BASE}/secure"

        const val LOGOUT = "/logout"
    }

    object Account {
        const val BASE = "/account"

        const val CURRENT = "/current"
    }

    object AiModel {
        const val BASE = "/ai-models"

        const val ALL = "/all"
        const val IS_MODEL_AVAILABLE = "/is-model-available/{modelName}"
    }

    object Chat {
        const val BASE = "/chats"

        /**Path Variables: publicChatId*/
        const val BY_PUBLIC_CHAT_ID = "/by-public-id/{publicChatId}"

        /**Path Variables: publicChatId*/
        const val BASE_BY_PUBLIC_CHAT_ID = "$BASE$BY_PUBLIC_CHAT_ID"

        /**Path Variables: publicChatId*/
        const val ALL = "/all"

        /**Path Variables: chatName*/
        const val BY_NAME = "/by-name/{chatName}"

        /**Path Variables: publicChatId*/
        const val MODEL = "$BY_PUBLIC_CHAT_ID/model"
    }

    object Message {
        /**Path Variables: publicChatId*/
        const val BASE = "${Chat.BASE_BY_PUBLIC_CHAT_ID}/messages"

        /**Path Variables: referenceId*/
        const val NEW = "/new/{referenceId}"
        /**Path Variables: referenceId*/
        const val OLD = "/old/{referenceId}"
        /**Path Variables: messageId*/
        const val GET_MESSAGE = "/{messageId}"
        const val GENERATE_RESPONSE = "/generate-response"
        const val DELETE = "/{messageId}"
        /**Path Variables: bound1, bound2*/
        const val BULK_DELETE = "/bulk-delete/{bound1}/{bound2}"
    }

    object Poll {
        const val BASE = "${Chat.BASE_BY_PUBLIC_CHAT_ID}/polls"

        const val START = "/start"
        const val VOTE = "/vote"
        const val ALL = "/all"
    }

}