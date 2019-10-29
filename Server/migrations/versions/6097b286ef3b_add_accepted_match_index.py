"""Add accepted match index

Revision ID: 6097b286ef3b
Revises: cbf0f0298763
Create Date: 2019-10-28 21:43:56.071423

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = '6097b286ef3b'
down_revision = 'cbf0f0298763'
branch_labels = None
depends_on = None


def upgrade():
    op.create_index('idx_accepted_match', 'accepted_matches', ['user1', 'user2'], unique=True)


def downgrade():
    op.drop_index('idx_accepted_match', table_name='accepted_matches')
