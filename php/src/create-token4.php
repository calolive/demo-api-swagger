<?php

require_once 'vendor/autoload.php';
use GuzzleHttp\Client;
use GuzzleHttp\Exception\RequestException;
include('utils.php');

session_start();
$base_url = $_SESSION['base_url_swagger'];
$token = $_SESSION['token'];
$cdi = $_SESSION['cdi'];
$contract_id = $_SESSION['contract_id'];
$contractor_id = intval($_GET['contractor']);
$actor_id = $_SESSION['actor_id'];

var_error_log($contractor_id);

$client = new Client([
    // You can set any number of default request options.s
    'timeout'  => 30.0,
]);

class Parameters {
    public $actor_id;
    public $contract_definition_id;
    public $recipient_id;
    public $validity_duration;

    function __construct($aid, $cdid, $rid, $vd) {
        $this->actor_id = $aid;
        $this->contract_definition_id = $cdid;
        $this->recipient_id = $rid;
        $this->validity_duration = $vd;
    }
}

$parameters = new Parameters($actor_id, $cdi, $contractor_id, round(microtime(true) * 1000) + 3000000);
//parameters = new Parameters($actor_id, $cdi, -1, round(microtime(true) * 1000) + 3000000);
//$parameters = new Parameters($actor_id, $cdi, $contractor_id);

//Create token -----------------------------------------------------------------------------
try {
    $resp_ct = $client->request('POST', "$base_url/contracts/$contract_id/transaction/temporarytoken?signonly=true", [
        'headers' => [
            'j_token' => $token,
            'Content-type' => 'application/json',
            'Accept' => 'application/json'
        ],
        'body' => json_encode($parameters)        
    ]);

    $resp_obj = json_decode($resp_ct->getBody()->getContents());
    $t_token = $resp_obj->token;

    $_SESSION['t_token'] = $t_token;
    $resp_json = array('code' => $resp_ct->getStatusCode(), 'response' => $t_token);
    echo json_encode($resp_json);
}
catch (RequestException $e) {
    $resp_json = array('code' => 400, 'response' => $e->getMessage());   
    echo json_encode($resp_json);
}

?>