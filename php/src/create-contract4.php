<?php

require_once 'vendor/autoload.php';
use GuzzleHttp\Client;
use GuzzleHttp\Exception\RequestException;
include('utils.php');

session_start();
$token = $_SESSION['token'];
$cdi = $_SESSION['cdi'];
$contractor_id = $_SESSION['contractor_id'];
$url = $_SESSION['base_url_swagger'];

$client = new Client(array('timeout' => 30.0));

class Contract {
    public $name = "contrat test omathieu";
    public $contract_definition_id;
    public $vendor_email = "fo.smtpc@calindasoftware.com";
    public $message_title = "Votre contrat Z pour signature";
    public $message_body = "Vous êtes signataire du contrat Z ci-joint pour la companyName. Merci de bien vouloir le signer électroniquement en cliquant sur le lien ci-dessous.<br>Cordialement";
    public $keep_on_move = false;
    public $auto_close = 1;

    function __construct($cdi) {
        $this->contract_definition_id = $cdi;
    }
}

class Recipient {
    public $civility;
    public $firstname;
    public $lastname;
    public $address_1;
    public $cell_phone;
    public $email;
    public $signature_mode;

    function __construct($civ, $first, $last, $addr, $cell, $mail, $sm) {
        $this->civility = $civ;
        $this->firstname = $first;
        $this->lastname = $last;
        $this->address_1 = $addr;
        $this->cell_phone = $cell;
        $this->email = $mail;
        $this->signature_mode = $sm;
    }
}

class Recipients {
    public $data ;

    function __construct($data) {
        $this->data = $data;
    }
}

$contract = new Contract($cdi);
$recipient1 = new Recipient("MONSIEUR", "Olivier", "Mathieu", "rue Bombée", "0662370619", "omathieu+567@calindasoftware.com", 10);
$recipient2 = new Recipient("MONSIEUR", "John", "Doe", "rue plate", "0662370619", "omathieu+436@calindasoftware.com", 10);
//$recipients = new Recipients(array($recipient1), array($recipient2));
$recipients = new Recipients(array($recipient1, $recipient2));
//$recipients = new Recipients(array($recipient1));
var_error_log(json_encode($recipients));

$payload = [
    'headers' => [
        'j_token' => $token,
        'Accept' => 'application/json',
    ],
    'multipart' => [
        [
            'name' => 'contract',
            'contents' => json_encode($contract, JSON_UNESCAPED_UNICODE),
            'headers' => ['Content-type' => 'application/json']
        ],
        [
            'name' => 'recipients',
            'contents' => json_encode($recipients, JSON_UNESCAPED_UNICODE),
            'headers' => ['Content-type' => 'application/json']
        ],
        [
            'name' => 'pdfparts',
            'contents' => fopen(__DIR__.'/resources/smartpacte2.pdf', 'r'),
            'filename' => 'contrat.pdf',
            'headers' => ['Content-type' => 'application/pdf']
        ]
        // [
        //     'name' => 'appendixparts',
        //     'contents' => fopen('c:\Users\olivm\Documents\billet.pdf', 'r'),
        //     'filename' => 'rib.pdf',
        //     'headers' => ['Content-type' => 'application/pdf']
        // ]
    ]
];

try {
    $response = $client->request(
        'POST',
        "$url/contracts/allinone?start=false",
        $payload);

    $resp_obj = json_decode($response->getBody()->getContents());

    $_SESSION['contract_id'] = $resp_obj->contract->contract_id;
    $infos['cdi'] = $resp_obj->contract->contract_definition_id;
    $infos['contract_id'] = $resp_obj->contract->contract_id;
    $infos['contractors'] = $resp_obj->recipients;

    $resp_json = array('code' => $response->getStatusCode(), 'response' => $infos);
    echo json_encode($resp_json);
}
catch (RequestException $e) {
    $resp_json = array('code' => 400, 'response' => $e->getMessage());
    echo json_encode($resp_json);
}

?>