// SPDX-License-Identifier: MIT
pragma solidity  ^0.8.26;

contract Mapping {

    mapping(address => uint256) public balanceOf;

    function deposit() external payable {
        require (msg.value > 0, "Please send more than 0 ETH");

        address sender = msg.sender; // contract's address
        balanceOf[sender] += msg.value;// add to mapping of that address with the value from deposit() function and save in a variable

    }

    function addBalance(address _userAddress, uint256 _balance) public {
        balanceOf[_userAddress] = _balance;
    }

    function getBalance(address _userAddress) public view returns(uint256) {
        return balanceOf[_userAddress];
    }

    struct UserData {
        string name;
        uint age;
        bool isMale;
    }

    UserData public me;

    function setUserData() public {
         me = UserData("Alex", 25, true);
    }

}