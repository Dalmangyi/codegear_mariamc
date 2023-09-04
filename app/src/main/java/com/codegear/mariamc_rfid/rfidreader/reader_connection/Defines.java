package com.codegear.mariamc_rfid.rfidreader.reader_connection;

public final class Defines {
    public static final int NO_ERROR = 0;

    public static final int ERROR_OPEN_NFC_ADAPTER = -100;

    public static final int INFO_ALREADY_PAIRED = 200;
    public static final int ERROR_PAIRING_FAILED = -200;
    public static final int ERROR_PAIRING_TIMEOUT = -201;

    public static final int INFO_UNPAIRING_NO_PAIRED = 220;
    public static final int ERROR_UNPAIRING_FAILED = -220;
    public static final int ERROR_UNPAIRING_TIMEOUT = -221;
    public static final String NameStartString = "RFD";


    /// Strings
    public static final String INFO_ALREADY_CONNECTED_STR = "RFD8500이 이미 연결되어 있습니다!";
    public static final String INFO_ALREADY_PAIRED_CONNECTING_STR = "RFD8500이 이미 페어링되었습니다! 연결 중";
    public static final String INFO_PAIRING = "페어링 ";
    public static final String INFO_DONE_PAIRING_CONNECTING_STR = "페어링 완료, 연결중";
    public static final String SUCCESS_CONNECTING_DONE_STR = "성공 - 연결 완료";
    public static final String CONFIRM_CONNECTION_ACTION_STR = "RFD8500의 노란색 트리거 버튼을 눌러 연결을 확인해 주세요!";
    public static final String INFO_CONNECTING_ABORTED_STR = "정보 - 연결이 중단됨";
    public static final String INFO_CONNECTING_TIMED_OUT_STR = "정보 - 연결 시간이 초과됨";
    public static final String ERROR_CONNECTING_FAILED_STR = "에러 - 연결 실패!";
    public static final String ERROR_PAIRING_FAILED_STR = "에러 - 페어링 실패!";
    public static final String ERROR_PAIRING_FAILED_TIMEOUT_STR = "에러 - 페어링 실패! (타임아웃)";

    public static final int BT_ADDRESS_LENGTH = 12;
}
