<?php

require_once 'vendor/autoload.php';
use GuzzleHttp\Client;
use GuzzleHttp\Exception\RequestException;

session_start();
$url = $_SESSION['base_url_swagger'];
$token = $_SESSION['token'];
$contract_id = $_SESSION['contract_id'];

$client = new Client([
    // You can set any number of default request options.
    'timeout'  => 30.0,
]);

//Get signed contract ------------------------------------------------
try {
    $resp_sc = $client->request('GET', "$url/contracts/$contract_id/transaction/signedcontract", [
        'query' => [
            'filename' => 'contract.pdf'
        ],
        'headers' => [

            'j_token' => $token,
            'Accept' => 'application/pdf'

        ]
    ]);

    $pdf = "data:application/pdf;base64," . base64_encode($resp_sc->getBody()->getContents());
    $resp_json = array('code' => $resp_sc->getStatusCode(), 'pdffile' => $pdf);
    echo json_encode($resp_json);
}
catch (RequestException $e) {
    $resp_json = array('code' => 400, 'pdffile' => $e->getMessage());   
    echo json_encode($resp_json);
}

?>