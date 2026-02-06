package com.yourRPG.chatPG.config

object ApplicationEndpoints {

    object Auth {
        const val BASE = "/auth"

        const val                    LOGIN = "/login"
        const val           REFRESH_TOKENS = "/refreshTokens"
        const val     OPEN_PASSWORD_CHANGE = "/open-password-change"
        const val  FULFILL_PASSWORD_CHANGE = "/fulfill-password-change"
        const val    OPEN_ACCOUNT_CREATION = "/open-account-creation"
        const val FULFILL_ACCOUNT_CREATION = "/fulfill-account-creation"
    }

    object AuthSecure {
        const val BASE = "${Auth.BASE}/secure"

        const val                LOGOUT = "/logout"
        const val REQUIRE_REFRESH_TOKEN = "/require-refresh-tokens"
    }

    object Account {
        const val BASE = "/account"

        const val CURRENT = "/current"
    }

    object AiModel {
        const val BASE = "/ai-models"

        const val                ALL = "/all"
        const val IS_MODEL_AVAILABLE = "/is-model-available"
    }

    object Chat {
        const val BASE = "/chats"

        const val          ALL = "/all"
        /**Path Variables: chatName*/
        const val      BY_NAME = "/by-name/{chatName}"
        const val CHOOSE_MODEL = "/choose-model"
        /**Path Variables: chatId*/
        const val        MODEL = "/{chatId}/model"
    }

    object Message {
        /**Path Variables: chatId*/
        const val BASE = "${Chat.BASE}/{chatId}/messages"

        /**Path Variables: referenceId*/
        const val               NEW = "/new/{referenceId}"
        /**Path Variables: referenceId*/
        const val               OLD = "/old/{referenceId}"
        /**Path Variables: messageId*/
        const val       GET_MESSAGE = "/{messageId}"
        const val GENERATE_RESPONSE = "/generate-response"
        const val            DELETE = "/{messageId}"
        /**Path Variables: bound1, bound2*/
        const val       BULK_DELETE = "/bulk-delete/{bound1}/{bound2}"
    }

    object Poll {
        const val BASE = "${Chat.BASE}/polls"

        const val START = "/start"
        const val  VOTE = "/vote"
        const val   ALL = "/all"
    }

}