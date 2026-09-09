"""Read-only key inspection for the pinned FTB migration research; not an importer."""

import argparse
import json
import re
import uuid


def decimal_id(value: str) -> str:
    """Accept canonical nonnegative signed-long keys without floating-point conversion."""
    if not isinstance(value, str) or not re.fullmatch(r"0|[1-9][0-9]{0,18}", value):
        raise ValueError("Expected a canonical decimal ID string")
    number = int(value)
    if number > 0x7FFFFFFFFFFFFFFF:
        raise ValueError("ID exceeds First Torch's positive signed-long range")
    return f"{number:016X}"


def claim_key(value: str) -> dict[str, str | None]:
    """Decode canonical QuestKey output; scope is not proof of migration ownership."""
    if not isinstance(value, str) or not re.fullmatch(r"[0-9a-f]{32}:[0-7][0-9A-F]{15}", value):
        raise ValueError("Expected undashed lowercase UUID:16-digit uppercase reward ID")
    owner = uuid.UUID(hex=value[:32])
    return {
        "scope": "team" if owner.int == 0 else "player",
        "player_uuid": None if owner.int == 0 else str(owner),
        "reward_id": value[33:],
    }


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("kind", choices=("decimal-id", "claim-key"))
    parser.add_argument("value")
    args = parser.parse_args()
    try:
        result = {"object_id": decimal_id(args.value)} if args.kind == "decimal-id" else claim_key(args.value)
    except ValueError as error:
        parser.error(str(error))
    print(json.dumps(result, indent=2))


if __name__ == "__main__":
    main()
