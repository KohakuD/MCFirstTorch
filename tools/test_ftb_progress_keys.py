"""Synthetic key fixtures only; no player saves or external dependencies."""

import unittest
from ftb_progress_keys import claim_key, decimal_id


class ProgressKeyTests(unittest.TestCase):
    def test_exact_above_double_precision(self):
        self.assertEqual("0020000000000001", decimal_id("9007199254740993"))

    def test_boundaries(self):
        self.assertEqual("0000000000000000", decimal_id("0"))
        self.assertEqual("7FFFFFFFFFFFFFFF", decimal_id("9223372036854775807"))

    def test_rejects_noncanonical_and_overflow_ids(self):
        for value in ("-1", "+1", "01", " 1", "1.0", "1e3", "0x10", "１", "9223372036854775808", "9" * 100, 1, True):
            with self.subTest(value=value), self.assertRaises(ValueError):
                decimal_id(value)

    def test_team_claim_has_no_player(self):
        self.assertEqual({"scope": "team", "player_uuid": None, "reward_id": "10A0B0C0D0E00001"},
                         claim_key("0" * 32 + ":10A0B0C0D0E00001"))

    def test_personal_claim_keeps_reward_id(self):
        result = claim_key("12345678123412341234123456789abc:10A0B0C0D0E00001")
        self.assertEqual("player", result["scope"])
        self.assertEqual("12345678-1234-1234-1234-123456789abc", result["player_uuid"])
        self.assertEqual("10A0B0C0D0E00001", result["reward_id"])

    def test_rejects_ambiguous_claim_keys(self):
        valid = "12345678123412341234123456789abc:10A0B0C0D0E00001"
        for value in (valid.replace(":", "-"), valid.lower(), valid.upper(), valid + " ",
                      valid[:-1], valid.replace(":1", ":8"), None):
            with self.subTest(value=value), self.assertRaises(ValueError):
                claim_key(value)


if __name__ == "__main__":
    unittest.main()
