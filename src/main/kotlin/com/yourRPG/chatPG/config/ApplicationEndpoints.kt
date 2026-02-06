package com.yourRPG.chatPG.config

object ApplicationEndpoints {

    object Auth {
        const val BASE = "/auth"

        const val                    LOGIN = "$BASE/login"
        const val           REFRESH_TOKENS = "$BASE/refreshTokens"
        const val     OPEN_PASSWORD_CHANGE = "$BASE/open-password-change"
        const val  FULFILL_PASSWORD_CHANGE = "$BASE/fulfill-password-change"
        const val    OPEN_ACCOUNT_CREATION = "$BASE/open-account-creation"
        const val FULFILL_ACCOUNT_CREATION = "$BASE/fulfill-account-creation"
    }

    object AuthSecure {
        const val BASE = "${Auth.BASE}/secure"

        const val                LOGOUT = "$BASE/logout"
        const val REQUIRE_REFRESH_TOKEN = "$BASE/require-refresh-tokens"
    }

    object Account {
        const val BASE = "/account"

        const val CURRENT = "$BASE/current"
    }

    object AiModel {
        const val BASE = "/ai-models"

        const val                ALL = "$BASE/all"
        const val IS_MODEL_AVAILABLE = "$BASE/is-model-available"
    }

    object Chat {
        const val BASE = "/chats"

        const val          ALL = "$BASE/all"
        /**Path Variables: chatName*/
        const val      BY_NAME = "$BASE/by-name/{chatName}"
        const val CHOOSE_MODEL = "$BASE/choose-model"
        /**Path Variables: chatId*/
        const val        MODEL = "$BASE/{chatId}/model"
    }

    object Message {
        /**Path Variables: chatId*/
        const val BASE = "${Chat.BASE}/{chatId}/messages"

        /**Path Variables: referenceId*/
        const val               NEW = "$BASE/new/{referenceId}"
        /**Path Variables: referenceId*/
        const val               OLD = "$BASE/old/{referenceId}"
        /**Path Variables: messageId*/
        const val       GET_MESSAGE = "$BASE/{messageId}"
        const val GENERATE_RESPONSE = "$BASE/generate-response"
        const val            DELETE = "$BASE/{messageId}"
        /**Path Variables: bound1, bound2*/
        const val       BULK_DELETE = "$BASE/bulk-delete/{bound1}/{bound2}"
    }

    object Poll {
        const val BASE = "${Chat.BASE}/polls"

        const val START = "$BASE/start"
        const val  VOTE = "$BASE/vote"
        const val   ALL = "$BASE/all"
    }

}