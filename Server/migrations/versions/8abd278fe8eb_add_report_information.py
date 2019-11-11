"""Add report information

Revision ID: 8abd278fe8eb
Revises: 6097b286ef3b
Create Date: 2019-11-04 12:04:01.529693

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = '8abd278fe8eb'
down_revision = '6097b286ef3b'
branch_labels = None
depends_on = None


def upgrade():
    op.create_table('report_information',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('sender', sa.Integer(), nullable=False),
    sa.Column('message', sa.Text(), nullable=False),
    sa.Column('time', sa.DateTime(), server_default=sa.text('(CURRENT_TIMESTAMP)'), nullable=False),
    sa.ForeignKeyConstraint(['sender'], ['user.id'], ),
    sa.PrimaryKeyConstraint('id')
    )


def downgrade():
    op.drop_table('report_information')
