"""Add picture field

Revision ID: 6002aa870692
Revises: 6097b286ef3b
Create Date: 2019-11-11 17:20:52.909450

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = '6002aa870692'
down_revision = '6097b286ef3b'
branch_labels = None
depends_on = None


def upgrade():
    op.add_column('user', sa.Column('picture', sa.LargeBinary(), nullable=True))


def downgrade():
    op.drop_column('user', 'picture')
